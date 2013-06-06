package sun.plugin.extension;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import sun.misc.ExtensionInfo;
import sun.misc.ExtensionInstallationException;
import sun.misc.ExtensionInstallationProvider;
import sun.security.action.GetPropertyAction;

public class ExtensionInstallationImpl
  implements ExtensionInstallationProvider
{
  public boolean installExtension(final ExtensionInfo paramExtensionInfo1, final ExtensionInfo paramExtensionInfo2)
    throws ExtensionInstallationException
  {
    Trace.msgExtPrintln("optpkg.install.info", new Object[] { paramExtensionInfo1 });
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final ExtensionInfo val$reqExtInfo;
        private final ExtensionInfo val$instExtInfo;

        public Object run()
          throws IOException, ExtensionInstallationException, CertificateException, CertificateEncodingException, CertificateExpiredException, CertificateNotYetValidException, CertificateParsingException, KeyStoreException, NoSuchAlgorithmException, IOException, CRLException, InvalidAlgorithmParameterException, InterruptedException
        {
          String str1 = ExtensionUtils.makePlatformDependent(ExtensionUtils.extractJarFileName(paramExtensionInfo1.url));
          String str2 = ExtensionUtils.makePlatformDependent(paramExtensionInfo1.url);
          ToolkitStore.getUI();
          if (ExtensionInstallationImpl.this.askUserForAcknowledgment(paramExtensionInfo1, paramExtensionInfo2) != 0)
            throw new ExtensionInstallationException("User denied installation of " + str2);
          URL localURL = new URL(str2);
          URLConnection localURLConnection = localURL.openConnection();
          InputStream localInputStream = localURLConnection.getInputStream();
          BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream);
          String str3 = ExtensionUtils.getTempDir() + File.separator + str1;
          File localFile = new File(str3);
          FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
          BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
          ExtensionUtils.copy(localBufferedInputStream, localBufferedOutputStream);
          localBufferedInputStream.close();
          localInputStream.close();
          localBufferedOutputStream.close();
          localFileOutputStream.close();
          ExtensionInstallationImpl.this.verifyJar(str2, str3);
          ExtensionInstallationImpl.this.installJarFile(str2, str3);
          if (localFile.exists())
            localFile.delete();
          return null;
        }
      });
    }
    catch (Throwable localThrowable)
    {
      Object localObject = localThrowable;
      String str = null;
      if ((localThrowable instanceof PrivilegedActionException))
      {
        localObject = ((PrivilegedActionException)localThrowable).getException();
        if ((localObject instanceof CertificateExpiredException))
          str = ResourceManager.getMessage("optpkg.cert_expired");
        else if ((localObject instanceof CertificateNotYetValidException))
          str = ResourceManager.getMessage("optpkg.cert_notyieldvalid");
        else if ((localObject instanceof CertificateException))
          str = ResourceManager.getMessage("optpkg.cert_notverify");
        else
          str = ResourceManager.getMessage("optpkg.general_error");
      }
      else
      {
        str = ResourceManager.getMessage("optpkg.general_error");
      }
      Trace.extPrintException((Throwable)localObject, str, ResourceManager.getMessage("optpkg.caption"));
      Trace.msgExtPrintln("optpkg.install.fail");
      return true;
    }
    Trace.msgExtPrintln("optpkg.install.ok");
    return true;
  }

  private void verifyJar(String paramString1, String paramString2)
    throws ExtensionInstallationException, CertificateException, CertificateEncodingException, CertificateExpiredException, CertificateNotYetValidException, CertificateParsingException, KeyStoreException, NoSuchAlgorithmException, IOException, CRLException, InvalidAlgorithmParameterException
  {
    JarFile localJarFile = null;
    try
    {
      HashMap localHashMap = new HashMap();
      byte[] arrayOfByte = new byte[8192];
      Object localObject1 = null;
      localJarFile = new JarFile(paramString2, true);
      Enumeration localEnumeration = localJarFile.entries();
      Object localObject3;
      Object localObject4;
      Object localObject5;
      while (localEnumeration.hasMoreElements())
      {
        localObject2 = (JarEntry)localEnumeration.nextElement();
        localObject3 = ((JarEntry)localObject2).getName();
        localObject4 = localJarFile.getInputStream((ZipEntry)localObject2);
        int i;
        while ((i = ((InputStream)localObject4).read(arrayOfByte, 0, arrayOfByte.length)) != -1);
        ((InputStream)localObject4).close();
        if ((!((String)localObject3).startsWith("META-INF/")) && (!((String)localObject3).endsWith("/")) && (((JarEntry)localObject2).getSize() != 0L))
        {
          localObject5 = ((JarEntry)localObject2).getCertificates();
          int j = (localObject5 != null) && (localObject5.length > 0) ? 1 : 0;
          if (j != 0)
          {
            if (localObject1 == null)
            {
              if (hasMultipleSigners((Certificate[])localObject5))
                throw new ExtensionInstallationException("Error: one entry has multiple certificates");
              localObject1 = localObject5;
            }
            else if (!equalChains(localObject1, (Certificate[])localObject5))
            {
              throw new ExtensionInstallationException("Error: Entries signed by different signer");
            }
            CodeSource localCodeSource = (CodeSource)localHashMap.get(localObject5);
            if (localCodeSource == null)
            {
              localCodeSource = new CodeSource(new URL(paramString1), (Certificate[])localObject5);
              localHashMap.put(localObject5, localCodeSource);
              if (TrustDecider.isAllPermissionGranted(localCodeSource, null) == 0L)
                throw new ExtensionInstallationException("User deny optional package installer to be launched.");
            }
          }
          else
          {
            throw new ExtensionInstallationException("Optional package installer is unsigned. (signatures missing or not parsable)");
          }
        }
      }
      Object localObject2 = localJarFile.getManifest();
      if (localObject2 != null)
      {
        localObject3 = ((Manifest)localObject2).getEntries().entrySet();
        localObject4 = ((Set)localObject3).iterator();
        while (((Iterator)localObject4).hasNext())
        {
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject4).next();
          localObject5 = (String)localEntry.getKey();
          if (localJarFile.getEntry((String)localObject5) == null)
            throw new ExtensionInstallationException("Manifest entry not in the JAR file");
        }
      }
      else
      {
        throw new ExtensionInstallationException("No manifest in the optional package installer.");
      }
    }
    catch (IOException localIOException)
    {
      throw new ExtensionInstallationException("IO Error. Unable to verify optional package installer.");
    }
    finally
    {
      if (localJarFile != null)
        localJarFile.close();
    }
  }

  private boolean hasMultipleSigners(Certificate[] paramArrayOfCertificate)
  {
    Object localObject = paramArrayOfCertificate[0];
    for (int i = 1; i < paramArrayOfCertificate.length; i++)
    {
      Certificate localCertificate = paramArrayOfCertificate[i];
      if (!isSigner((Certificate)localObject, localCertificate))
        return true;
      localObject = localCertificate;
    }
    return false;
  }

  private boolean isSigner(Certificate paramCertificate1, Certificate paramCertificate2)
  {
    try
    {
      paramCertificate1.verify(paramCertificate2.getPublicKey());
      return true;
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      return false;
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      return false;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return false;
    }
    catch (SignatureException localSignatureException)
    {
      return false;
    }
    catch (CertificateException localCertificateException)
    {
    }
    return false;
  }

  private boolean equalChains(Certificate[] paramArrayOfCertificate1, Certificate[] paramArrayOfCertificate2)
  {
    if (paramArrayOfCertificate1.length != paramArrayOfCertificate2.length)
      return false;
    for (int i = 0; i < paramArrayOfCertificate1.length; i++)
      if (!paramArrayOfCertificate1[i].equals(paramArrayOfCertificate2[i]))
        return false;
    return true;
  }

  private int askUserForAcknowledgment(ExtensionInfo paramExtensionInfo1, ExtensionInfo paramExtensionInfo2)
  {
    String str1 = null;
    AppInfo localAppInfo = new AppInfo();
    Object localObject;
    if (paramExtensionInfo2 != null)
    {
      String str2 = paramExtensionInfo1.name;
      if (paramExtensionInfo1.title != null)
        str2 = paramExtensionInfo1.title;
      int j = paramExtensionInfo2.isCompatibleWith(paramExtensionInfo1);
      Object[] arrayOfObject;
      switch (j)
      {
      case 1:
        str1 = ResourceManager.getMessage("optpkg.prompt_user.text");
        localObject = new MessageFormat(ResourceManager.getMessage("optpkg.prompt_user.specification"));
        arrayOfObject = new Object[] { paramExtensionInfo1.specVersion };
        localAppInfo.setTitle(str2 + ((MessageFormat)localObject).format(arrayOfObject));
        break;
      case 2:
        str1 = ResourceManager.getMessage("optpkg.prompt_user.text");
        localObject = new MessageFormat(ResourceManager.getMessage("optpkg.prompt_user.implementation"));
        arrayOfObject = new Object[] { paramExtensionInfo1.implementationVersion };
        localAppInfo.setTitle(str2 + ((MessageFormat)localObject).format(arrayOfObject));
        break;
      case 3:
        str1 = ResourceManager.getMessage("optpkg.prompt_user.text");
        localAppInfo.setTitle(str2 + " (" + paramExtensionInfo1.vendor + ")");
        break;
      default:
        localAppInfo.setTitle(paramExtensionInfo1.name);
        str1 = ResourceManager.getMessage("optpkg.prompt_user.default.text");
      }
    }
    else
    {
      localAppInfo.setTitle(paramExtensionInfo1.name);
      str1 = ResourceManager.getMessage("optpkg.prompt_user.default.text");
    }
    ToolkitStore.getUI();
    int i = -1;
    if (!Trace.isAutomationEnabled())
    {
      try
      {
        localAppInfo.setFrom(new URL(paramExtensionInfo1.url));
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
      String str3 = ResourceManager.getString("common.ok_btn");
      localObject = ResourceManager.getString("common.cancel_btn");
      ToolkitStore.getUI();
      i = ToolkitStore.getUI().showMessageDialog(null, localAppInfo, 2, ResourceManager.getMessage("optpkg.prompt_user.caption"), str1, null, null, str3, (String)localObject, null);
    }
    else
    {
      Trace.msgExtPrintln("optpkg.install.automation");
      ToolkitStore.getUI();
      i = 0;
    }
    ToolkitStore.getUI();
    if (i == 0)
      Trace.msgExtPrintln("optpkg.install.granted", new Object[] { paramExtensionInfo1.url });
    else
      Trace.msgExtPrintln("optpkg.install.deny");
    return i;
  }

  private void installJarFile(String paramString1, String paramString2)
    throws ExtensionInstallationException, IOException, InterruptedException
  {
    Trace.msgExtPrintln("optpkg.install.begin", new Object[] { paramString2 });
    JarFile localJarFile = new JarFile(paramString2);
    Manifest localManifest = localJarFile.getManifest();
    if (localManifest != null)
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("java.ext.dirs"));
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
      String str2 = localStringTokenizer.nextToken();
      Attributes localAttributes = localManifest.getMainAttributes();
      Object localObject = new RawExtensionInstaller();
      if (localAttributes != null)
        if (localAttributes.getValue(Attributes.Name.MAIN_CLASS) != null)
          localObject = new JavaExtensionInstaller();
        else if (localAttributes.getValue(Attributes.Name.EXTENSION_INSTALLATION) != null)
          localObject = new NativeExtensionInstaller();
      localJarFile = null;
      ((ExtensionInstaller)localObject).install(paramString1, paramString2, str2);
    }
  }

  private static String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  private static int getAcceleratorKey(String paramString)
  {
    return ResourceManager.getAcceleratorKey(paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.extension.ExtensionInstallationImpl
 * JD-Core Version:    0.6.2
 */