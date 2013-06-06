package com.sun.deploy.security;

import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.Preloader;
import com.sun.applet2.preloader.event.UserDeclinedEvent;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.SecuritySettings;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.DeployLock;
import com.sun.deploy.util.PerfLogger;
import com.sun.deploy.util.URLUtil;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.Timestamp;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.certpath.OCSP;
import sun.security.provider.certpath.OCSP.RevocationStatus;
import sun.security.provider.certpath.OCSP.RevocationStatus.CertStatus;
import sun.security.validator.PKIXValidator;
import sun.security.validator.Validator;
import sun.security.validator.ValidatorException;

public class TrustDecider
{
  public static final int TrustOption_GrantThisSession = 0;
  public static final int TrustOption_Deny = 1;
  public static final int TrustOption_GrantAlways = 2;
  private static CertStore rootStore = null;
  private static CertStore permanentStore = null;
  private static CertStore sandboxStore = null;
  private static CertStore sessionStore = null;
  private static CertStore sessionSandboxStore = null;
  private static CertStore deniedStore = null;
  private static CertStore browserRootStore = null;
  private static CertStore browserTrustedStore = null;
  private static LazyRootStore lazyRootStore = null;
  private static List jurisdictionList = null;
  private static X509CRL crl509 = null;
  private static boolean ocspValidConfig = false;
  private static String ocspSigner = null;
  private static String ocspURL = null;
  private static boolean crlCheck = false;
  private static boolean ocspCheck = false;
  private static boolean ocspEECheck = false;
  private static DeployLock deployLock = null;
  public static final long PERMISSION_DENIED = 0L;
  public static final long PERMISSION_GRANTED_FOR_SESSION = 1L;
  public static final long PERMISSION_UNKNOWN = 2L;
  private static final String SUN_NAMESPACE = "OU=Java Signed Extensions,OU=Corporate Object Signing,O=Sun Microsystems Inc";
  private static final String ORACLE_NAMESPACE = "OU=Java Signed Extensions,OU=Corporate Object Signing,O=Oracle Corporation";
  private static final String[] PRE_TRUSTED_NAMESPACES = { "OU=Java Signed Extensions,OU=Corporate Object Signing,O=Sun Microsystems Inc", "OU=Java Signed Extensions,OU=Corporate Object Signing,O=Oracle Corporation" };
  private static final List preTrustList = Arrays.asList(PRE_TRUSTED_NAMESPACES);
  private static boolean storesLoaded = false;
  private static boolean reloadDeniedStore = false;

  protected static void grabDeployLock()
    throws InterruptedException
  {
    deployLock.lock();
  }

  protected static void releaseDeployLock()
  {
    try
    {
      deployLock.unlock();
    }
    catch (IllegalMonitorStateException localIllegalMonitorStateException)
    {
    }
  }

  public static void resetDenyStore()
  {
    Trace.msgSecurityPrintln("trustdecider.check.reset.denystore");
    try
    {
      grabDeployLock();
      deniedStore = new DeniedCertStore();
      reloadDeniedStore = true;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new RuntimeException(localInterruptedException);
    }
    finally
    {
      releaseDeployLock();
    }
  }

  // ERROR //
  public static void reset()
  {
    // Byte code:
    //   0: ldc 21
    //   2: invokestatic 861	com/sun/deploy/util/PerfLogger:setTime	(Ljava/lang/String;)V
    //   5: invokestatic 824	com/sun/deploy/security/TrustDecider:grabDeployLock	()V
    //   8: iconst_0
    //   9: putstatic 771	com/sun/deploy/security/TrustDecider:storesLoaded	Z
    //   12: invokestatic 821	com/sun/deploy/security/RootCertStore:getCertStore	()Lcom/sun/deploy/security/CertStore;
    //   15: putstatic 776	com/sun/deploy/security/TrustDecider:rootStore	Lcom/sun/deploy/security/CertStore;
    //   18: invokestatic 814	com/sun/deploy/security/DeploySigningCertStore:getCertStore	()Lcom/sun/deploy/security/CertStore;
    //   21: putstatic 775	com/sun/deploy/security/TrustDecider:permanentStore	Lcom/sun/deploy/security/CertStore;
    //   24: invokestatic 815	com/sun/deploy/security/DeploySigningCertStore:getSandboxCertStore	()Lcom/sun/deploy/security/CertStore;
    //   27: putstatic 777	com/sun/deploy/security/TrustDecider:sandboxStore	Lcom/sun/deploy/security/CertStore;
    //   30: new 448	com/sun/deploy/security/SessionCertStore
    //   33: dup
    //   34: ldc 27
    //   36: invokespecial 822	com/sun/deploy/security/SessionCertStore:<init>	(Ljava/lang/String;)V
    //   39: putstatic 779	com/sun/deploy/security/TrustDecider:sessionStore	Lcom/sun/deploy/security/CertStore;
    //   42: new 448	com/sun/deploy/security/SessionCertStore
    //   45: dup
    //   46: ldc 6
    //   48: invokespecial 822	com/sun/deploy/security/SessionCertStore:<init>	(Ljava/lang/String;)V
    //   51: putstatic 778	com/sun/deploy/security/TrustDecider:sessionSandboxStore	Lcom/sun/deploy/security/CertStore;
    //   54: new 443	com/sun/deploy/security/DeniedCertStore
    //   57: dup
    //   58: invokespecial 812	com/sun/deploy/security/DeniedCertStore:<init>	()V
    //   61: putstatic 774	com/sun/deploy/security/TrustDecider:deniedStore	Lcom/sun/deploy/security/CertStore;
    //   64: ldc 31
    //   66: invokestatic 803	com/sun/deploy/config/Config:getBooleanProperty	(Ljava/lang/String;)Z
    //   69: ifeq +25 -> 94
    //   72: invokestatic 849	com/sun/deploy/services/ServiceManager:getService	()Lcom/sun/deploy/services/Service;
    //   75: astore_0
    //   76: aload_0
    //   77: invokeinterface 936 1 0
    //   82: putstatic 772	com/sun/deploy/security/TrustDecider:browserRootStore	Lcom/sun/deploy/security/CertStore;
    //   85: aload_0
    //   86: invokeinterface 937 1 0
    //   91: putstatic 773	com/sun/deploy/security/TrustDecider:browserTrustedStore	Lcom/sun/deploy/security/CertStore;
    //   94: new 446	com/sun/deploy/security/LazyRootStore
    //   97: dup
    //   98: getstatic 772	com/sun/deploy/security/TrustDecider:browserRootStore	Lcom/sun/deploy/security/CertStore;
    //   101: getstatic 776	com/sun/deploy/security/TrustDecider:rootStore	Lcom/sun/deploy/security/CertStore;
    //   104: invokespecial 819	com/sun/deploy/security/LazyRootStore:<init>	(Lcom/sun/deploy/security/CertStore;Lcom/sun/deploy/security/CertStore;)V
    //   107: putstatic 780	com/sun/deploy/security/TrustDecider:lazyRootStore	Lcom/sun/deploy/security/LazyRootStore;
    //   110: goto +8 -> 118
    //   113: astore_0
    //   114: aload_0
    //   115: invokevirtual 867	java/lang/Exception:printStackTrace	()V
    //   118: new 450	com/sun/deploy/security/TrustDecider$1
    //   121: dup
    //   122: invokespecial 844	com/sun/deploy/security/TrustDecider$1:<init>	()V
    //   125: invokestatic 887	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;
    //   128: pop
    //   129: goto +8 -> 137
    //   132: astore_0
    //   133: aload_0
    //   134: invokevirtual 893	java/security/PrivilegedActionException:printStackTrace	()V
    //   137: new 451	com/sun/deploy/security/TrustDecider$2
    //   140: dup
    //   141: invokespecial 845	com/sun/deploy/security/TrustDecider$2:<init>	()V
    //   144: invokestatic 887	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;
    //   147: pop
    //   148: goto +8 -> 156
    //   151: astore_0
    //   152: aload_0
    //   153: invokevirtual 893	java/security/PrivilegedActionException:printStackTrace	()V
    //   156: jsr +22 -> 178
    //   159: goto +25 -> 184
    //   162: astore_0
    //   163: new 476	java/lang/RuntimeException
    //   166: dup
    //   167: aload_0
    //   168: invokespecial 871	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   171: athrow
    //   172: astore_1
    //   173: jsr +5 -> 178
    //   176: aload_1
    //   177: athrow
    //   178: astore_2
    //   179: invokestatic 825	com/sun/deploy/security/TrustDecider:releaseDeployLock	()V
    //   182: ret 2
    //   184: ldc 10
    //   186: invokestatic 861	com/sun/deploy/util/PerfLogger:setTime	(Ljava/lang/String;)V
    //   189: return
    //
    // Exception table:
    //   from	to	target	type
    //   94	110	113	java/lang/Exception
    //   118	129	132	java/security/PrivilegedActionException
    //   137	148	151	java/security/PrivilegedActionException
    //   5	156	162	java/lang/InterruptedException
    //   5	159	172	finally
    //   162	176	172	finally
  }

  public static long isAllPermissionGranted(CodeSource paramCodeSource, Preloader paramPreloader)
    throws CertificateEncodingException, CertificateExpiredException, CertificateNotYetValidException, CertificateParsingException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, CRLException, InvalidAlgorithmParameterException
  {
    return isAllPermissionGranted(paramCodeSource, new AppInfo(), false, paramPreloader);
  }

  private static void notifyOnUserDeclined(Preloader paramPreloader, String paramString)
  {
    if (paramPreloader == null)
      paramPreloader = (Preloader)ToolkitStore.get().getAppContext().get("preloader_key");
    try
    {
      if (paramPreloader != null)
        paramPreloader.handleEvent(new UserDeclinedEvent(paramString));
    }
    catch (CancelException localCancelException)
    {
    }
  }

  private static void doCheckRevocationStatus(X509Certificate[] paramArrayOfX509Certificate, Date paramDate, String paramString, boolean paramBoolean)
    throws KeyStoreException, CertificateException, NoSuchAlgorithmException
  {
    if (!permanentStore.contains(paramArrayOfX509Certificate[0], paramString, paramBoolean))
      try
      {
        OCSP.RevocationStatus.CertStatus localCertStatus = doOCSPEEValidation(paramArrayOfX509Certificate[0], paramArrayOfX509Certificate[1], lazyRootStore, paramDate);
        if (localCertStatus != OCSP.RevocationStatus.CertStatus.GOOD)
        {
          Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.bad");
          throw new CertificateException(ResourceManager.getMessage("trustdecider.check.ocsp.ee.revoked"));
        }
        Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.good");
      }
      catch (IOException localIOException)
      {
        Trace.msgSecurityPrintln(localIOException.getMessage());
      }
      catch (CertPathValidatorException localCertPathValidatorException)
      {
        Trace.msgSecurityPrintln(localCertPathValidatorException.getMessage());
        throw new CertificateException(localCertPathValidatorException);
      }
      catch (NoClassDefFoundError localNoClassDefFoundError)
      {
      }
  }

  protected static List breakDownMultiSignerChains(Certificate[] paramArrayOfCertificate)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    ArrayList localArrayList1 = new ArrayList();
    while (j < paramArrayOfCertificate.length)
    {
      ArrayList localArrayList2 = new ArrayList();
      for (int m = i; (m + 1 < paramArrayOfCertificate.length) && ((paramArrayOfCertificate[m] instanceof X509Certificate)) && ((paramArrayOfCertificate[(m + 1)] instanceof X509Certificate)) && (CertUtils.isIssuerOf((X509Certificate)paramArrayOfCertificate[m], (X509Certificate)paramArrayOfCertificate[(m + 1)])); m++);
      j = m + 1;
      for (int n = i; n < j; n++)
        localArrayList2.add(paramArrayOfCertificate[n]);
      localArrayList1.add(localArrayList2);
      i = j;
      k++;
    }
    return localArrayList1;
  }

  private static boolean haveValidatorSupport()
  {
    if (Config.isJavaVersionAtLeast16())
      try
      {
        Class localClass = Class.forName("sun.security.validator.Validator", true, ClassLoader.getSystemClassLoader());
        if (localClass != null)
          return true;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Trace.msgSecurityPrintln("trustdecider.check.validate.notfound");
      }
    return false;
  }

  private static boolean hasCRL(X509Certificate[] paramArrayOfX509Certificate)
    throws IOException
  {
    if (crl509 != null)
      return true;
    for (int i = 0; i < paramArrayOfX509Certificate.length; i++)
      if (CertUtils.getCertCRLExtension(paramArrayOfX509Certificate[i]))
        return true;
    return false;
  }

  private static boolean hasOCSP(X509Certificate[] paramArrayOfX509Certificate)
    throws IOException
  {
    if (ocspValidConfig)
      return true;
    for (int i = 0; i < paramArrayOfX509Certificate.length; i++)
      if (CertUtils.hasAIAExtensionWithOCSPAccessMethod(paramArrayOfX509Certificate[i]))
        return true;
    return false;
  }

  public static synchronized void validateChainForWarmup(X509Certificate[] paramArrayOfX509Certificate, CodeSource paramCodeSource, int paramInt, AppInfo paramAppInfo, boolean paramBoolean)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, CRLException, InvalidAlgorithmParameterException
  {
    try
    {
      grabDeployLock();
      ensureBasicStoresLoaded();
      long l = validateChain(paramArrayOfX509Certificate, paramCodeSource, paramInt, paramAppInfo, paramBoolean, null);
      Trace.println("Warmup validation completed (res=" + l + ")", TraceLevel.SECURITY);
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    finally
    {
      releaseDeployLock();
    }
  }

  private static long validateChain(X509Certificate[] paramArrayOfX509Certificate, CodeSource paramCodeSource, int paramInt, AppInfo paramAppInfo, boolean paramBoolean, Preloader paramPreloader)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, CRLException, InvalidAlgorithmParameterException
  {
    ValidationState localValidationState = getValidationState(paramArrayOfX509Certificate, paramCodeSource, paramInt, paramAppInfo, paramBoolean, paramPreloader, false);
    if (localValidationState.trustDecision != 2L)
      return localValidationState.trustDecision;
    return askUser(paramArrayOfX509Certificate, paramCodeSource, localValidationState, paramAppInfo, paramBoolean, paramPreloader);
  }

  public static ValidationState getValidationState(X509Certificate[] paramArrayOfX509Certificate, CodeSource paramCodeSource, int paramInt, AppInfo paramAppInfo, boolean paramBoolean1, Preloader paramPreloader, boolean paramBoolean2)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, CRLException, InvalidAlgorithmParameterException
  {
    boolean bool1 = crlCheck;
    boolean bool2 = ocspCheck;
    boolean bool3 = ocspEECheck;
    String str1 = getLocString(paramCodeSource.getLocation());
    ValidationState localValidationState = new ValidationState();
    for (int i = 0; i < paramArrayOfX509Certificate.length; i++)
    {
      long l = paramArrayOfX509Certificate[i].getNotAfter().getTime();
      if (l < localValidationState.expirationDate)
        localValidationState.expirationDate = l;
      try
      {
        paramArrayOfX509Certificate[i].checkValidity();
      }
      catch (CertificateExpiredException localCertificateExpiredException)
      {
        if (localValidationState.certExpiredException == null)
        {
          localValidationState.certExpiredException = localCertificateExpiredException;
          localValidationState.certValidity = -1;
          localValidationState.certValidityNoTS = -1;
          localValidationState.timeValid = false;
        }
      }
      catch (CertificateNotYetValidException localCertificateNotYetValidException)
      {
        if (localValidationState.certNotYetValidException == null)
        {
          localValidationState.certNotYetValidException = localCertificateNotYetValidException;
          localValidationState.certValidity = 1;
          localValidationState.certValidityNoTS = 1;
          localValidationState.timeValid = false;
        }
      }
      BlacklistedCerts.check(paramArrayOfX509Certificate[i]);
    }
    i = paramArrayOfX509Certificate.length;
    X509Certificate localX509Certificate = paramArrayOfX509Certificate[(i - 1)];
    X500Principal localX500Principal1 = localX509Certificate.getIssuerX500Principal();
    X500Principal localX500Principal2 = localX509Certificate.getSubjectX500Principal();
    PerfLogger.setTime("Security: End check certificate expired and start replace CA check");
    Object localObject1 = lazyRootStore.getTrustAnchors(localX509Certificate);
    if (localObject1 == null)
    {
      localValidationState.rootCANotValid = true;
      localObject1 = new ArrayList();
      ((List)localObject1).add(localX509Certificate);
    }
    PerfLogger.setTime("Security: End replace CA check and start timestamp check");
    localValidationState.timeStampInfo = getTimeStampInfo(paramCodeSource, paramInt, paramArrayOfX509Certificate, lazyRootStore, localValidationState.timeValid);
    if (localValidationState.timeStampInfo != null)
    {
      localValidationState.timeValid = true;
      localValidationState.certValidity = 0;
    }
    PerfLogger.setTime("Security: End timestamp check and start pre-trusted certificate check");
    if ((!localValidationState.rootCANotValid) && (localValidationState.certValidityNoTS == 0) && (paramAppInfo.getType() == 3) && (!paramBoolean1) && (!permanentStore.contains(paramArrayOfX509Certificate[0], str1, localValidationState.timeValid)))
    {
      bool3 = true;
      Trace.msgSecurityPrintln("trustdecider.check.extensioninstall.on");
    }
    boolean bool4 = false;
    boolean bool5 = false;
    Object localObject2 = null;
    try
    {
      bool4 = (bool1) && (hasCRL(paramArrayOfX509Certificate));
      bool5 = (bool2) && (hasOCSP(paramArrayOfX509Certificate));
      PerfLogger.setTime("Security: Start getting validator class");
      Validator localValidator = Validator.getInstance("PKIX", "plugin code signing", (Collection)localObject1);
      localObject3 = (PKIXValidator)localValidator;
      localObject2 = ((PKIXValidator)localObject3).getParameters();
      ((PKIXParameters)localObject2).addCertPathChecker(new DeployCertPathChecker((PKIXValidator)localObject3));
      PerfLogger.setTime("Security: End getting validator class and start CRL revocation check");
      if (bool1)
      {
        Trace.msgSecurityPrintln("trustdecider.check.validation.crl.on");
        localObject2 = doCRLValidation((PKIXParameters)localObject2, bool4);
      }
      else
      {
        Trace.msgSecurityPrintln("trustdecider.check.validation.crl.off");
      }
      PerfLogger.setTime("Security: End CRL and start OCSP revocation check");
      if (bool2)
      {
        Trace.msgSecurityPrintln("trustdecider.check.validation.ocsp.on");
        doOCSPValidation((PKIXParameters)localObject2, lazyRootStore, paramArrayOfX509Certificate, bool5, bool1);
      }
      else
      {
        Trace.msgSecurityPrintln("trustdecider.check.validation.ocsp.off");
      }
      PerfLogger.setTime("Security: End OCSP revocation check and start validator class");
      localObject4 = new X509Certificate[i];
      for (int j = 0; j < i; j++)
        localObject4[j] = new X509CertificateWrapper(paramArrayOfX509Certificate[j]);
      localValidator.validate((X509Certificate[])localObject4);
      PerfLogger.setTime("Security: End call validator class");
      if (((bool1) && (bool4)) || ((bool2) && (bool5)))
        Trace.msgSecurityPrintln("trustdecider.check.revocation.succeed");
    }
    catch (CertificateException localCertificateException)
    {
      Object localObject3;
      Object localObject4;
      if ((localCertificateException instanceof ValidatorException))
      {
        localObject3 = (ValidatorException)localCertificateException;
        if (ValidatorException.T_NO_TRUST_ANCHOR.equals(((ValidatorException)localObject3).getErrorType()))
        {
          localValidationState.rootCANotValid = true;
        }
        else
        {
          localObject4 = "Certificate has been revoked";
          if (((bool1) && (bool4)) || ((bool2) && (bool5)))
          {
            String str3 = ((ValidatorException)localObject3).getMessage();
            if (str3.contains((CharSequence)localObject4))
              Trace.msgSecurityPrintln("trustdecider.check.validation.revoked");
            else
              Trace.msgSecurityPrintln(str3);
            throw ((Throwable)localObject3);
          }
          throw ((Throwable)localObject3);
        }
      }
      else
      {
        throw localCertificateException;
      }
    }
    catch (IOException localIOException)
    {
      Trace.msgSecurityPrintln(localIOException.getMessage());
      throw localIOException;
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      Trace.msgSecurityPrintln(localInvalidAlgorithmParameterException.getMessage());
      throw localInvalidAlgorithmParameterException;
    }
    catch (CRLException localCRLException)
    {
      Trace.msgSecurityPrintln(localCRLException.getMessage());
      throw localCRLException;
    }
    finally
    {
      Security.setProperty("com.sun.security.onlyCheckRevocationOfEECert", "false");
    }
    PerfLogger.setTime("Security: End certificate validation and start OCSP End-Entity revocation check");
    if ((bool3) && (!bool2) && (i > 1) && (!localValidationState.rootCANotValid) && (localValidationState.certValidityNoTS == 0))
      doCheckRevocationStatus(paramArrayOfX509Certificate, ((PKIXParameters)localObject2).getDate(), str1, localValidationState.timeValid);
    else
      Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.off");
    PerfLogger.setTime("Security: End OCSP End-Entity revocation check");
    if (deniedStore.contains(paramArrayOfX509Certificate[0], str1, localValidationState.timeValid))
    {
      String str2 = paramCodeSource.getLocation() != null ? paramCodeSource.getLocation().toString() : null;
      notifyOnUserDeclined(paramPreloader, str2);
      localValidationState.trustDecision = 0L;
      return localValidationState;
    }
    if ((localValidationState.rootCANotValid) && (checkTrustedExtension(paramArrayOfX509Certificate[0])))
    {
      localValidationState.trustDecision = 2L;
      return localValidationState;
    }
    if (permanentStore.contains(paramArrayOfX509Certificate[0], str1, localValidationState.timeValid))
    {
      localValidationState.trustDecision = localValidationState.expirationDate;
      return localValidationState;
    }
    if (sessionStore.contains(paramArrayOfX509Certificate[0], str1, localValidationState.timeValid))
    {
      localValidationState.trustDecision = 1L;
      return localValidationState;
    }
    if (paramBoolean2)
    {
      if (sandboxStore.contains(paramArrayOfX509Certificate[0], str1, localValidationState.timeValid))
      {
        localValidationState.trustDecision = localValidationState.expirationDate;
        return localValidationState;
      }
      if (sessionSandboxStore.contains(paramArrayOfX509Certificate[0], str1, localValidationState.timeValid))
      {
        localValidationState.trustDecision = 1L;
        return localValidationState;
      }
    }
    if ((browserTrustedStore != null) && (browserTrustedStore.contains(paramArrayOfX509Certificate[0])))
    {
      localValidationState.trustDecision = 1L;
      return localValidationState;
    }
    localValidationState.trustDecision = 2L;
    return localValidationState;
  }

  static boolean checkTrustedExtension(X509Certificate paramX509Certificate)
  {
    Trace.msgSecurityPrintln("trustdecider.check.trustextension.jurisdiction");
    X500Principal localX500Principal = paramX509Certificate.getSubjectX500Principal();
    String str1 = localX500Principal.getName();
    Iterator localIterator = preTrustList.iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      if (str1.endsWith(str2))
      {
        Trace.msgSecurityPrintln("trustdecider.check.trustextension.jurisdiction.found");
        return true;
      }
    }
    return false;
  }

  private static long askUser(X509Certificate[] paramArrayOfX509Certificate, CodeSource paramCodeSource, ValidationState paramValidationState, AppInfo paramAppInfo, boolean paramBoolean, Preloader paramPreloader)
    throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException
  {
    String str = getLocString(paramCodeSource.getLocation());
    releaseDeployLock();
    if ((paramValidationState.rootCANotValid) && (!SecuritySettings.isAskGrantSelfSignedSet()))
      throw new CertificateException(ResourceManager.getMessage("trustdecider.user.cannot.grant.notinca"));
    if (!SecuritySettings.isAskGrantShowSet())
      throw new CertificateException(ResourceManager.getMessage("trustdecider.user.cannot.grant.any"));
    int i = X509Util.showSecurityDialog(paramArrayOfX509Certificate, paramCodeSource.getLocation(), 0, paramArrayOfX509Certificate.length, paramValidationState.rootCANotValid, paramValidationState.certValidity, paramValidationState.timeStampInfo, paramAppInfo, paramBoolean);
    try
    {
      grabDeployLock();
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new RuntimeException("Intermediate error trying to perform security validation");
    }
    PerfLogger.setTime("Security: Start take action on security dialog box");
    if (i == 0)
    {
      Trace.msgSecurityPrintln("trustdecider.user.grant.session");
      sessionStore.add(paramArrayOfX509Certificate[0], str, paramValidationState.timeValid);
      sessionStore.save();
      paramValidationState.trustDecision = 1L;
    }
    else
    {
      CertStore localCertStore;
      if (i == 2)
      {
        Trace.msgSecurityPrintln("trustdecider.user.grant.forever");
        localCertStore = DeploySigningCertStore.getUserCertStore();
        localCertStore.load(true);
        if (localCertStore.add(paramArrayOfX509Certificate[0], str, paramValidationState.timeValid))
          localCertStore.save();
        storesLoaded = false;
        paramValidationState.trustDecision = paramValidationState.expirationDate;
      }
      else
      {
        Trace.msgSecurityPrintln("trustdecider.user.deny");
        deniedStore.add(paramArrayOfX509Certificate[0], str, paramValidationState.timeValid);
        deniedStore.save();
        localCertStore = paramCodeSource.getLocation() != null ? paramCodeSource.getLocation().toString() : null;
        notifyOnUserDeclined(paramPreloader, localCertStore);
        paramValidationState.trustDecision = 0L;
      }
    }
    PerfLogger.setTime("Security: End take action on security dialog box");
    return paramValidationState.trustDecision;
  }

  protected static void recordSandboxAnswer(Certificate[] paramArrayOfCertificate, CodeSource paramCodeSource, ValidationState paramValidationState, Preloader paramPreloader, int paramInt)
  {
    int i = 0;
    try
    {
      String str1 = getLocString(paramCodeSource.getLocation());
      if (paramInt == 0)
      {
        sessionSandboxStore.add(paramArrayOfCertificate[0], str1, paramValidationState.timeValid);
        sessionSandboxStore.save();
      }
      else if (paramInt == 2)
      {
        sandboxStore.load(true);
        if (sandboxStore.add(paramArrayOfCertificate[0], str1, paramValidationState.timeValid))
          sandboxStore.save();
      }
      else
      {
        deniedStore.add(paramArrayOfCertificate[0], str1, paramValidationState.timeValid);
        deniedStore.save();
        i = 1;
        String str2 = paramCodeSource.getLocation() != null ? paramCodeSource.getLocation().toString() : null;
        notifyOnUserDeclined(paramPreloader, str2);
      }
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    if (i != 0)
      throw new SecurityException("user declined to run signed sandbox app", null);
  }

  protected static void ensureBasicStoresLoaded()
    throws InterruptedException, IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    if ((reloadDeniedStore) || (!storesLoaded))
    {
      deniedStore.load();
      reloadDeniedStore = false;
    }
    if (storesLoaded)
      return;
    storesLoaded = true;
    PerfLogger.setTime("Security: Start loading JRE permanent certStore");
    permanentStore.load();
    sandboxStore.load();
    PerfLogger.setTime("Security: End loading JRE permanent certStore");
    sessionStore.load();
    sessionSandboxStore.load();
    PerfLogger.setTime("Security: start loading browser Trust certStore");
    if (browserTrustedStore != null)
      browserTrustedStore.load();
    PerfLogger.setTime("Security: End loading browser Trust certStore");
  }

  public static synchronized long isAllPermissionGranted(CodeSource paramCodeSource, AppInfo paramAppInfo, boolean paramBoolean, Preloader paramPreloader)
    throws CertificateEncodingException, CertificateExpiredException, CertificateNotYetValidException, CertificateParsingException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, CRLException, InvalidAlgorithmParameterException
  {
    try
    {
      grabDeployLock();
      Certificate[] arrayOfCertificate = paramCodeSource.getCertificates();
      if (arrayOfCertificate == null)
      {
        long l1 = 0L;
        return l1;
      }
      ensureBasicStoresLoaded();
      List localList1 = breakDownMultiSignerChains(arrayOfCertificate);
      PerfLogger.setTime("Security: End break certificate chain");
      long l2;
      if (haveValidatorSupport())
      {
        Trace.msgSecurityPrintln("trustdecider.check.validate.certpath.algorithm");
        l2 = 0L;
        Iterator localIterator = localList1.iterator();
        for (int i = 0; localIterator.hasNext(); i++)
        {
          PerfLogger.setTime("Security: Start check certificate expired");
          List localList2 = (List)localIterator.next();
          X509Certificate[] arrayOfX509Certificate = (X509Certificate[])localList2.toArray(new X509Certificate[0]);
          l2 = validateChain(arrayOfX509Certificate, paramCodeSource, i, paramAppInfo, paramBoolean, paramPreloader);
          if (l2 != 0L)
          {
            long l3 = l2;
            return l3;
          }
        }
      }
      else
      {
        Trace.msgSecurityPrintln("trustdecider.check.validate.legacy.algorithm");
        rootStore.load();
        if (browserRootStore != null)
          browserRootStore.load();
        if (CertValidator.validate(paramCodeSource, paramAppInfo, arrayOfCertificate, localList1.size(), rootStore, browserRootStore, browserTrustedStore, sessionStore, permanentStore, deniedStore))
        {
          l2 = 1L;
          return l2;
        }
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new RuntimeException(localInterruptedException);
    }
    finally
    {
      releaseDeployLock();
    }
    return 0L;
  }

  private static boolean checkTSAPath(CertPath paramCertPath, LazyRootStore paramLazyRootStore)
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    Trace.msgSecurityPrintln("trustdecider.check.timestamping.tsapath");
    List localList1 = paramCertPath.getCertificates();
    X509Certificate[] arrayOfX509Certificate = (X509Certificate[])localList1.toArray(new X509Certificate[0]);
    int i = arrayOfX509Certificate.length;
    X509Certificate localX509Certificate = arrayOfX509Certificate[(i - 1)];
    List localList2 = paramLazyRootStore.getTrustAnchors(localX509Certificate);
    if (localList2 == null)
      return false;
    Validator localValidator = Validator.getInstance("PKIX", "tsa server", localList2);
    try
    {
      arrayOfX509Certificate = localValidator.validate(arrayOfX509Certificate);
    }
    catch (CertificateException localCertificateException)
    {
      Trace.msgSecurityPrintln(localCertificateException.getMessage());
      return false;
    }
    return true;
  }

  private static PKIXParameters doCRLValidation(PKIXParameters paramPKIXParameters, boolean paramBoolean)
    throws IOException, InvalidAlgorithmParameterException, CRLException, NoSuchAlgorithmException
  {
    if (crl509 != null)
    {
      Trace.msgSecurityPrintln("trustdecider.check.validation.crl.system.on");
      System.clearProperty("com.sun.security.enableCRLDP");
      paramPKIXParameters.setRevocationEnabled(true);
      paramPKIXParameters.addCertStore(java.security.cert.CertStore.getInstance("Collection", new CollectionCertStoreParameters(Collections.singletonList(crl509))));
    }
    else
    {
      Trace.msgSecurityPrintln("trustdecider.check.validation.crl.system.off");
      paramPKIXParameters.setRevocationEnabled(paramBoolean);
      System.setProperty("com.sun.security.enableCRLDP", Boolean.toString(paramBoolean));
    }
    return paramPKIXParameters;
  }

  private static void doOCSPValidation(PKIXParameters paramPKIXParameters, LazyRootStore paramLazyRootStore, X509Certificate[] paramArrayOfX509Certificate, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    X509Certificate localX509Certificate = null;
    boolean bool = false;
    Security.setProperty("ocsp.enable", Boolean.toString(paramBoolean1));
    if (ocspValidConfig)
      Security.setProperty("ocsp.responderURL", ocspURL);
    paramPKIXParameters.setRevocationEnabled(paramBoolean1);
    if (ocspValidConfig)
    {
      Trace.msgSecurityPrintln("trustdecider.check.validation.ocsp.system.on");
      bool = paramLazyRootStore.containSubject(ocspSigner);
      localX509Certificate = paramLazyRootStore.getOCSPCert();
      if ((bool) && (localX509Certificate != null))
        Security.setProperty("ocsp.responderCertSubjectName", localX509Certificate.getSubjectX500Principal().getName());
    }
    else
    {
      Trace.msgSecurityPrintln("trustdecider.check.validation.ocsp.system.off");
    }
    if ((!paramBoolean2) && (paramBoolean1))
      System.setProperty("com.sun.security.enableCRLDP", "true");
  }

  private static OCSP.RevocationStatus.CertStatus doOCSPEEValidation(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, LazyRootStore paramLazyRootStore, Date paramDate)
    throws IOException, CertPathValidatorException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.start");
    boolean bool = false;
    URI localURI = null;
    X509Certificate localX509Certificate = paramX509Certificate2;
    if (ocspValidConfig)
    {
      try
      {
        localURI = new URI(ocspURL);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.responderURI.no");
        return OCSP.RevocationStatus.CertStatus.GOOD;
      }
      bool = paramLazyRootStore.containSubject(ocspSigner);
      if (bool)
        localX509Certificate = paramLazyRootStore.getOCSPCert();
    }
    else
    {
      localURI = OCSP.getResponderURI(paramX509Certificate1);
    }
    if (localURI == null)
    {
      Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.responderURI.no");
      return OCSP.RevocationStatus.CertStatus.GOOD;
    }
    Object localObject = localURI.toString();
    Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.responderURI.value", new Object[] { localObject });
    localObject = OCSP.check(paramX509Certificate1, paramX509Certificate2, localURI, localX509Certificate, paramDate).getCertStatus();
    String str = ((OCSP.RevocationStatus.CertStatus)localObject).name();
    Trace.msgSecurityPrintln("trustdecider.check.ocsp.ee.return.status", new Object[] { str });
    return localObject;
  }

  private static Date getTimeStampInfo(CodeSource paramCodeSource, int paramInt, X509Certificate[] paramArrayOfX509Certificate, LazyRootStore paramLazyRootStore, boolean paramBoolean)
  {
    Date localDate1 = null;
    if (paramBoolean)
    {
      Trace.msgSecurityPrintln("trustdecider.check.timestamping.noneed");
      return null;
    }
    try
    {
      Trace.msgSecurityPrintln("trustdecider.check.timestamping.need");
      CodeSigner[] arrayOfCodeSigner = paramCodeSource.getCodeSigners();
      Timestamp localTimestamp = arrayOfCodeSigner[paramInt].getTimestamp();
      if (localTimestamp != null)
      {
        Trace.msgSecurityPrintln("trustdecider.check.timestamping.yes");
        localDate1 = localTimestamp.getTimestamp();
        CertPath localCertPath = localTimestamp.getSignerCertPath();
        Trace.msgSecurityPrintln("trustdecider.check.timestamping.need");
        Date localDate2 = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)].getNotAfter();
        Date localDate3 = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)].getNotBefore();
        if ((localDate1.before(localDate2)) && (localDate1.after(localDate3)))
        {
          Trace.msgSecurityPrintln("trustdecider.check.timestamping.valid");
          if (!checkTSAPath(localCertPath, paramLazyRootStore))
            localDate1 = null;
        }
        else
        {
          Trace.msgSecurityPrintln("trustdecider.check.timestamping.invalid");
          localDate1 = null;
        }
      }
      else
      {
        Trace.msgSecurityPrintln("trustdecider.check.timestamping.no");
      }
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      Trace.msgSecurityPrintln("trustdecider.check.timestamping.notfound");
    }
    catch (KeyStoreException localKeyStoreException)
    {
      Trace.msgSecurityPrintln("trustdecider.check.timestamping.notfound");
    }
    catch (IOException localIOException)
    {
      Trace.msgSecurityPrintln("trustdecider.check.timestamping.notfound");
    }
    catch (CertificateException localCertificateException)
    {
      Trace.msgSecurityPrintln("trustdecider.check.timestamping.notfound");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      Trace.msgSecurityPrintln("trustdecider.check.timestamping.notfound");
    }
    return localDate1;
  }

  protected static String getLocString(URL paramURL)
  {
    try
    {
      return paramURL.getProtocol() + "//" + paramURL.getHost() + ":" + URLUtil.getPort(paramURL);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    return null;
  }

  static
  {
    deployLock = new DeployLock();
    reset();
  }

  public static class ValidationState
  {
    public long trustDecision = 2L;
    public boolean rootCANotValid = false;
    public boolean timeValid = true;
    public long expirationDate = 9223372036854775807L;
    public int certValidity = 0;
    public int certValidityNoTS = 0;
    public CertificateExpiredException certExpiredException = null;
    public CertificateNotYetValidException certNotYetValidException = null;
    public Date timeStampInfo = null;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.TrustDecider
 * JD-Core Version:    0.6.2
 */