package com.sun.javaws.security;

import com.sun.applet2.preloader.Preloader;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.CachedCertificatesHelper;
import com.sun.deploy.security.SandboxSecurity;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.exceptions.JNLPSigningException;
import com.sun.javaws.exceptions.LaunchDescException;
import com.sun.javaws.exceptions.UnsignedAccessViolationException;
import com.sun.javaws.jnl.ExtensionDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.LaunchDescFactory;
import com.sun.javaws.jnl.ResourceVisitor;
import com.sun.javaws.jnl.ResourcesDesc;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JNLPSignedResourcesHelper
{
  static final boolean DEBUG = (Config.getDeployDebug()) || (Config.getPluginDebug());
  LaunchDesc mainDesc = null;
  private Thread warmupValidationThread = null;
  private boolean warmupOk = true;
  private static final String SIGNED_JNLP_ENTRY = "JNLP-INF/APPLICATION.JNLP";
  private static final String SIGNED_JNLP_TEMPLATE = "JNLP-INF/APPLICATION_TEMPLATE.JNLP";

  public JNLPSignedResourcesHelper(LaunchDesc paramLaunchDesc)
  {
    this.mainDesc = paramLaunchDesc;
    AppPolicy.createInstance(this.mainDesc.getCanonicalHome().getHost());
  }

  public synchronized void warmup()
  {
    if (this.warmupOk)
    {
      WarmupValidator localWarmupValidator = new WarmupValidator();
      this.warmupValidationThread = new Thread(localWarmupValidator);
      this.warmupValidationThread.setDaemon(true);
      this.warmupValidationThread.start();
    }
  }

  public void checkSignedLaunchDesc()
    throws IOException, JNLPException
  {
    checkSignedLaunchDesc(null, null);
  }

  public void checkSignedLaunchDesc(URL paramURL1, URL paramURL2)
    throws IOException, JNLPException
  {
    ArrayList localArrayList = new ArrayList();
    addExtensions(localArrayList, this.mainDesc);
    for (int i = 0; i < localArrayList.size(); i++)
    {
      LaunchDesc localLaunchDesc = (LaunchDesc)localArrayList.get(i);
      checkSignedLaunchDescHelper(localLaunchDesc, paramURL1, paramURL2);
    }
  }

  synchronized void ensureWarmupFinished()
  {
    if (this.warmupValidationThread != null)
    {
      try
      {
        this.warmupValidationThread.join();
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
      this.warmupValidationThread = null;
      this.warmupOk = false;
    }
  }

  public boolean checkSignedResources(Preloader paramPreloader, boolean paramBoolean)
    throws IOException, JNLPException, ExitException
  {
    ensureWarmupFinished();
    boolean bool = true;
    ArrayList localArrayList = new ArrayList();
    addExtensions(localArrayList, this.mainDesc);
    for (int i = 0; i < localArrayList.size(); i++)
    {
      LaunchDesc localLaunchDesc = (LaunchDesc)localArrayList.get(i);
      bool = (checkSignedResourcesHelper(localLaunchDesc, paramPreloader, paramBoolean)) && (bool);
    }
    return bool;
  }

  private void addExtensions(ArrayList paramArrayList, LaunchDesc paramLaunchDesc)
  {
    if (paramLaunchDesc == null)
      return;
    paramArrayList.add(paramLaunchDesc);
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc != null)
      localResourcesDesc.visit(new ResourceVisitor()
      {
        private final ArrayList val$list;

        public void visitExtensionDesc(ExtensionDesc paramAnonymousExtensionDesc)
        {
          if (!paramAnonymousExtensionDesc.isInstaller())
            JNLPSignedResourcesHelper.this.addExtensions(this.val$list, paramAnonymousExtensionDesc.getExtensionDesc());
        }
      });
  }

  private void checkSignedLaunchDescHelper(LaunchDesc paramLaunchDesc, URL paramURL1, URL paramURL2)
    throws IOException, JNLPException
  {
    boolean bool = paramLaunchDesc.isApplicationDescriptor();
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    Object localObject = null;
    try
    {
      arrayOfByte2 = getSignedJNLPFile(paramLaunchDesc, bool, true);
      if (arrayOfByte2 != null)
        try
        {
          paramLaunchDesc.checkSigningTemplate(arrayOfByte2);
          if (DEBUG)
            Trace.println("Signed JNLP Template matches LaunchDesc", TraceLevel.SECURITY);
          return;
        }
        catch (JNLPSigningException localJNLPSigningException)
        {
          if (DEBUG)
            Trace.println("Signed JNLP Template fails to match ld", TraceLevel.SECURITY);
          localObject = localJNLPSigningException;
        }
      arrayOfByte1 = getSignedJNLPFile(paramLaunchDesc, bool, false);
      if (arrayOfByte1 == null)
      {
        if (localObject != null)
          throw localObject;
      }
      else
      {
        LaunchDesc localLaunchDesc = LaunchDescFactory.buildDescriptor(arrayOfByte1, paramURL1, paramURL2, paramLaunchDesc.getLocation());
        if (Trace.isEnabled(TraceLevel.SECURITY))
        {
          Trace.println("Downloaded JNLP file: ", TraceLevel.SECURITY);
          Trace.println(paramLaunchDesc.toString(), TraceLevel.SECURITY);
          Trace.println("Signed JNLP file: ", TraceLevel.SECURITY);
          Trace.println(localLaunchDesc.toString(), TraceLevel.SECURITY);
        }
        paramLaunchDesc.checkSigning(localLaunchDesc);
        return;
      }
    }
    catch (LaunchDescException localLaunchDescException)
    {
      localLaunchDescException.setIsSignedLaunchDesc();
      throw localLaunchDescException;
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (JNLPException localJNLPException)
    {
      throw localJNLPException;
    }
    if ((paramLaunchDesc.getCachedCertificates() != null) && (paramLaunchDesc.getCachedCertificates()[0].isSignedJNLP()))
      throw new JNLPSigningException(paramLaunchDesc, null);
  }

  private static boolean hasProgressResources(ResourcesDesc paramResourcesDesc)
  {
    JARDesc[] arrayOfJARDesc = paramResourcesDesc.getLocalJarDescs();
    for (int i = 0; i < arrayOfJARDesc.length; i++)
      if (arrayOfJARDesc[i].isProgressJar())
        return true;
    return false;
  }

  private static boolean checkSignedResourcesHelper(LaunchDesc paramLaunchDesc, Preloader paramPreloader, boolean paramBoolean)
    throws IOException, JNLPException, ExitException
  {
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc == null)
      return true;
    if ((paramBoolean) && (!hasProgressResources(localResourcesDesc)))
      return true;
    if (paramLaunchDesc.isSecure())
    {
      try
      {
        SandboxSecurity.isPermissionGranted(getMainJarCodeSource(paramLaunchDesc), paramLaunchDesc.getAppInfo(), paramPreloader);
      }
      catch (SecurityException localSecurityException)
      {
        throw new ExitException(localSecurityException.getMessage(), localSecurityException, 0);
      }
      return paramLaunchDesc.isSecureJVMArgs();
    }
    JARDesc[] arrayOfJARDesc = localResourcesDesc.getLocalJarDescs();
    boolean bool = true;
    int i = 0;
    int j = 1;
    Object localObject1 = null;
    URL localURL1 = paramLaunchDesc.getCanonicalHome();
    int k = 0;
    URL localURL2 = null;
    SigningInfo localSigningInfo = null;
    Map localMap = null;
    if (DEBUG)
      Trace.println("Validating signatures for " + paramLaunchDesc.getLocation() + " " + paramLaunchDesc.getSourceURL(), TraceLevel.SECURITY);
    if (paramLaunchDesc.getLocation() != null)
    {
      localObject2 = paramLaunchDesc.getSourceURL();
      if (localObject2 == null)
        localObject2 = paramLaunchDesc.getLocation();
      localSigningInfo = new SigningInfo((URL)localObject2, paramLaunchDesc.getVersion());
      localMap = localSigningInfo.getTrustedEntries();
      if (DEBUG)
        Trace.println("TrustedSet " + (localMap != null ? Integer.toString(localMap.size()) : "null"), TraceLevel.SECURITY);
    }
    Object localObject2 = new SigningInfo[arrayOfJARDesc.length];
    HashMap localHashMap = new HashMap();
    int n = 0;
    int m;
    if (localMap == null)
    {
      m = 0;
      if (DEBUG)
        Trace.println("Empty trusted set for [" + localURL1 + "]", TraceLevel.SECURITY);
    }
    else
    {
      m = 1;
    }
    Object localObject3;
    Object localObject4;
    for (int i1 = 0; (n == 0) && (i1 < arrayOfJARDesc.length); i1++)
    {
      localObject3 = arrayOfJARDesc[i1];
      localObject2[i1] = new SigningInfo(((JARDesc)localObject3).getLocation(), ((JARDesc)localObject3).getVersion());
      if (DEBUG)
        Trace.println("Round 1 (" + i1 + " out of " + arrayOfJARDesc.length + "):" + ((JARDesc)localObject3).getLocationString(), TraceLevel.SECURITY);
      if (localObject2[i1].isFileKnownToBeNotCached())
      {
        if (DEBUG)
          Trace.println("    Skip: " + ((JARDesc)localObject3).getLocationString(), TraceLevel.SECURITY);
        i = 1;
      }
      else if (localObject2[i1].isKnownToBeValidated())
      {
        long l1 = localObject2[i1].getCachedVerificationTimestampt();
        String str1 = ((JARDesc)localObject3).getLocationString();
        if (!localObject2[i1].isKnownToBeSigned())
        {
          n = 1;
          m = 0;
        }
        else
        {
          if (m != 0)
          {
            localObject4 = (Long)localMap.get(str1);
            if ((localObject4 == null) || (((Long)localObject4).longValue() != l1))
            {
              if (DEBUG)
                Trace.println("Entry [" + str1 + ", " + localObject4 + "] does not match trusted set. Revert to full validation of JNLP.", TraceLevel.SECURITY);
              m = 0;
            }
          }
          localHashMap.put(str1, new Long(l1));
        }
      }
      else
      {
        n = 1;
        m = 0;
        if (DEBUG)
          Trace.println("Entry [" + ((JARDesc)localObject3).getLocationString() + "] is not prevalidated. Revert to full validation of this JAR.", TraceLevel.SECURITY);
      }
    }
    if (m == 0)
    {
      for (i1 = 0; (i1 < arrayOfJARDesc.length) && (j != 0); i1++)
      {
        localObject3 = arrayOfJARDesc[i1];
        if (DEBUG)
          Trace.println("Round 2 (" + i1 + " out of " + arrayOfJARDesc.length + "):" + ((JARDesc)localObject3).getLocationString(), TraceLevel.SECURITY);
        if (localObject2[i1] == null)
          localObject2[i1] = new SigningInfo(((JARDesc)localObject3).getLocation(), ((JARDesc)localObject3).getVersion());
        if (localObject2[i1].isFileKnownToBeNotCached())
        {
          if (DEBUG)
            Trace.println("    Skip " + ((JARDesc)localObject3).getLocationString(), TraceLevel.SECURITY);
          i = 1;
        }
        else
        {
          List localList2 = null;
          if (localObject2[i1].isKnownToBeValidated())
            localList2 = localObject2[i1].getCertificates();
          else
            localList2 = localObject2[i1].check();
          if (!localObject2[i1].isJarKnownToBeEmpty())
          {
            Resource localResource;
            if (localList2 == null)
            {
              bool = false;
              localURL2 = ((JARDesc)localObject3).getLocation();
              if (paramLaunchDesc.getSecurityModel() == 0)
                break;
              localResource = ResourceProvider.get().getCachedResource(((JARDesc)localObject3).getLocation(), ((JARDesc)localObject3).getVersion());
              ResourceProvider.get().markRetired(localResource, true);
              break;
            }
            if (localObject1 == null)
            {
              localObject1 = localList2;
            }
            else
            {
              localObject1 = SigningInfo.overlapChainLists(localList2, (List)localObject1);
              if (DEBUG)
                Trace.println("Have " + (localObject1 == null ? 0 : ((List)localObject1).size()) + " common certificates after processing " + ((JARDesc)localObject3).getLocationString(), TraceLevel.SECURITY);
              if (localObject1 == null)
              {
                j = 0;
                if (paramLaunchDesc.getSecurityModel() != 0)
                {
                  localResource = ResourceProvider.get().getCachedResource(((JARDesc)localObject3).getLocation(), ((JARDesc)localObject3).getVersion());
                  ResourceProvider.get().markRetired(localResource, true);
                }
              }
            }
            long l3 = localObject2[i1].getCachedVerificationTimestampt();
            localObject4 = ((JARDesc)localObject3).getLocationString();
            localHashMap.put(localObject4, new Long(l3));
            k++;
          }
        }
      }
      if (!paramLaunchDesc.isSecure())
      {
        if (!bool)
          throw new UnsignedAccessViolationException(paramLaunchDesc, localURL2, true);
        if (j == 0)
          throw new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.singlecertviolation"), null);
        List localList1 = normalizeCertificateList((List)localObject1);
        if (paramLaunchDesc.getCachedCertificates() != null)
        {
          localObject3 = paramLaunchDesc.getCachedCertificates();
          for (int i2 = 0; i2 < localObject3.length; i2++)
            checkCachedChain(paramLaunchDesc, localList1, localObject3[i2].getCertPath());
        }
        if (k > 0)
        {
          localObject3 = null;
          localObject3 = new CodeSource(paramLaunchDesc.getCanonicalHome(), (Certificate[])localList1.toArray(new Certificate[localList1.size()]));
          long l2 = AppPolicy.getInstance().grantUnrestrictedAccess(paramLaunchDesc, (CodeSource)localObject3, paramPreloader);
          if (l2 > 0L)
          {
            long l4 = System.currentTimeMillis();
            for (int i3 = 0; i3 < localObject2.length; i3++)
            {
              localObject2[i3].updateCacheIfNeeded(true, null, l4, l2);
              String str2 = arrayOfJARDesc[i3].getLocationString();
              if (localHashMap.containsKey(str2))
                localHashMap.put(str2, new Long(localObject2[i3].getCachedVerificationTimestampt()));
            }
            if (localSigningInfo != null)
              localSigningInfo.updateCache(true, localHashMap, System.currentTimeMillis(), l2);
          }
          m = 1;
        }
        else
        {
          if (localSigningInfo != null)
            localSigningInfo.updateCache(true, localHashMap, System.currentTimeMillis(), 9223372036854775807L);
          m = 1;
        }
      }
    }
    if ((m != 0) && (i == 0))
      paramLaunchDesc.setTrusted();
    if (DEBUG)
      Trace.println("LD - All JAR files signed: " + localURL1, TraceLevel.BASIC);
    return bool;
  }

  static byte[] getSignedJNLPFile(LaunchDesc paramLaunchDesc, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, JNLPException
  {
    if (paramLaunchDesc.getResources() == null)
      return null;
    JARDesc localJARDesc = paramLaunchDesc.getResources().getMainJar(paramBoolean1);
    if (localJARDesc == null)
      return null;
    return getSignedJNLPBits(localJARDesc.getLocation(), localJARDesc.getVersion(), paramBoolean2);
  }

  static byte[] getSignedJNLPBits(URL paramURL, String paramString, boolean paramBoolean)
    throws IOException
  {
    String str = paramBoolean ? "JNLP-INF/APPLICATION_TEMPLATE.JNLP" : "JNLP-INF/APPLICATION.JNLP";
    JarFile localJarFile = null;
    try
    {
      localJarFile = new JarFile(ResourceProvider.get().getCachedResourceFilePath(paramURL, paramString), false);
      Object localObject1 = localJarFile.getJarEntry(str);
      if (localObject1 == null)
      {
        localObject2 = localJarFile.entries();
        while ((((Enumeration)localObject2).hasMoreElements()) && (localObject1 == null))
        {
          localObject3 = (JarEntry)((Enumeration)localObject2).nextElement();
          if (((JarEntry)localObject3).getName().equalsIgnoreCase(str))
            localObject1 = localObject3;
        }
      }
      if (localObject1 == null)
      {
        localObject2 = null;
        return localObject2;
      }
      Object localObject2 = new byte[(int)((JarEntry)localObject1).getSize()];
      Object localObject3 = new DataInputStream(localJarFile.getInputStream((ZipEntry)localObject1));
      ((DataInputStream)localObject3).readFully((byte[])localObject2, 0, (int)((JarEntry)localObject1).getSize());
      ((DataInputStream)localObject3).close();
      Object localObject4 = localObject2;
      return localObject4;
    }
    finally
    {
      if (localJarFile != null)
        localJarFile.close();
    }
  }

  static void checkCachedChain(LaunchDesc paramLaunchDesc, List paramList, CertPath paramCertPath)
    throws LaunchDescException
  {
    if (paramList == null)
      return;
    List localList = paramCertPath.getCertificates();
    for (int i = 0; i < localList.size(); i++)
    {
      Certificate localCertificate = (Certificate)localList.get(i);
      if (!paramList.contains(localCertificate))
        throw new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.unmatched.embedded.cert"), null);
    }
  }

  static List normalizeCertificateList(List paramList)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramList != null)
      for (int i = 0; i < paramList.size(); i++)
      {
        Object localObject;
        if ((Config.isJavaVersionAtLeast15()) && ((paramList.get(i) instanceof CodeSigner)))
        {
          localObject = (CodeSigner)paramList.get(i);
          CertPath localCertPath = ((CodeSigner)localObject).getSignerCertPath();
          if (localCertPath != null)
            localArrayList.addAll(localCertPath.getCertificates());
        }
        else if ((paramList.get(i) instanceof SigningInfo.CertChain))
        {
          localObject = (SigningInfo.CertChain)paramList.get(i);
          localArrayList.addAll(Arrays.asList(((SigningInfo.CertChain)localObject).getCertificates()));
        }
      }
    return Collections.unmodifiableList(localArrayList);
  }

  private static CodeSource getMainJarCodeSource(LaunchDesc paramLaunchDesc)
  {
    Certificate[] arrayOfCertificate = null;
    JARDesc localJARDesc = paramLaunchDesc.getResources().getMainJar(true);
    if (localJARDesc != null)
      try
      {
        SigningInfo localSigningInfo = new SigningInfo(localJARDesc.getLocation(), localJARDesc.getVersion());
        List localList = normalizeCertificateList(localSigningInfo.check());
        arrayOfCertificate = (Certificate[])localList.toArray(new Certificate[localList.size()]);
      }
      catch (Exception localException)
      {
      }
    return new CodeSource(paramLaunchDesc.getCanonicalHome(), arrayOfCertificate);
  }

  class WarmupValidator
    implements Runnable
  {
    WarmupValidator()
    {
    }

    public void run()
    {
      if (JNLPSignedResourcesHelper.DEBUG)
        Trace.println("Staring warmup validation", TraceLevel.SECURITY);
      ArrayList localArrayList = new ArrayList();
      JNLPSignedResourcesHelper.this.addExtensions(localArrayList, JNLPSignedResourcesHelper.this.mainDesc);
      for (int i = 0; i < localArrayList.size(); i++)
      {
        LaunchDesc localLaunchDesc = (LaunchDesc)localArrayList.get(i);
        try
        {
          processSingleDesc(localLaunchDesc);
        }
        catch (Exception localException)
        {
          Trace.ignored(localException);
        }
      }
    }

    private void processSingleDesc(LaunchDesc paramLaunchDesc)
      throws GeneralSecurityException, IOException
    {
      CachedCertificatesHelper[] arrayOfCachedCertificatesHelper = paramLaunchDesc.getCachedCertificates();
      if (arrayOfCachedCertificatesHelper != null)
        for (int i = 0; i < arrayOfCachedCertificatesHelper.length; i++)
        {
          X509Certificate[] arrayOfX509Certificate = (X509Certificate[])arrayOfCachedCertificatesHelper[i].getCertPath().getCertificates().toArray(new X509Certificate[0]);
          CodeSource localCodeSource = new CodeSource(paramLaunchDesc.getCanonicalHome(), arrayOfX509Certificate);
          TrustDecider.validateChainForWarmup(arrayOfX509Certificate, localCodeSource, i, paramLaunchDesc.getAppInfo(), arrayOfCachedCertificatesHelper[i].isSignedJNLP());
        }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.security.JNLPSignedResourcesHelper
 * JD-Core Version:    0.6.2
 */