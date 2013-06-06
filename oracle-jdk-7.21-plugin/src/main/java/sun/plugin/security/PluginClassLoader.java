package sun.plugin.security;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.CeilingPolicy;
import com.sun.deploy.security.SecureCookiePermission;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.URLUtil;
import java.awt.AWTPermission;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import sun.applet.AppletClassLoader;
import sun.net.ProgressSource;
import sun.net.www.ParseUtil;
import sun.plugin.util.ProgressMonitor;

public final class PluginClassLoader extends AppletClassLoader
{
  private static RuntimePermission usePolicyPermission;
  private URL base;
  private HashMap JARJARtoJAR = new HashMap();

  public PluginClassLoader(URL paramURL)
  {
    super(paramURL);
    this.base = paramURL;
  }

  public URL getBaseURL()
  {
    return this.base;
  }

  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    URL localURL = paramCodeSource.getLocation();
    if ((localURL != null) && (localURL.getProtocol().equals("file")))
    {
      localObject1 = ParseUtil.decode(localURL.getFile());
      if (localObject1 != null)
      {
        localObject1 = ((String)localObject1).replace('/', File.separatorChar);
        localObject2 = File.separator + System.getProperty("java.home") + File.separator + "axbridge" + File.separator + "lib";
        try
        {
          localObject1 = new File((String)localObject1).getCanonicalPath();
          localObject2 = new File((String)localObject2).getCanonicalPath();
          if ((localObject1 != null) && (localObject2 != null) && (((String)localObject1).startsWith((String)localObject2)))
          {
            localPermissionCollection.add(new AllPermission());
            return localPermissionCollection;
          }
        }
        catch (IOException localIOException)
        {
        }
      }
    }
    Object localObject1 = null;
    Object localObject2 = (Policy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Policy.getPolicy();
      }
    });
    localObject1 = ((Policy)localObject2).getPermissions(paramCodeSource);
    localPermissionCollection.add(new RuntimePermission("accessClassInPackage.sun.audio"));
    localPermissionCollection.add(new PropertyPermission("browser", "read"));
    localPermissionCollection.add(new PropertyPermission("browser.version", "read"));
    localPermissionCollection.add(new PropertyPermission("browser.vendor", "read"));
    localPermissionCollection.add(new PropertyPermission("http.agent", "read"));
    localPermissionCollection.add(new PropertyPermission("javapi.*", "read,write"));
    localPermissionCollection.add(new PropertyPermission("javaws.*", "read,write"));
    localPermissionCollection.add(new PropertyPermission("jnlp.*", "read,write"));
    localPermissionCollection.add(new PropertyPermission("javaplugin.version", "read"));
    if (usePolicyPermission == null)
      usePolicyPermission = new RuntimePermission("usePolicy");
    if ((!((PermissionCollection)localObject1).implies(usePolicyPermission)) && (paramCodeSource.getCertificates() != null))
      try
      {
        if (TrustDecider.isAllPermissionGranted(paramCodeSource, null) != 0L)
          CeilingPolicy.addTrustedPermissions(localPermissionCollection);
      }
      catch (CertificateExpiredException localCertificateExpiredException)
      {
        Trace.securityPrintException(localCertificateExpiredException, ResourceManager.getMessage("rsa.cert_expired"), ResourceManager.getMessage("security.dialog.caption"));
      }
      catch (CertificateNotYetValidException localCertificateNotYetValidException)
      {
        Trace.securityPrintException(localCertificateNotYetValidException, ResourceManager.getMessage("rsa.cert_notyieldvalid"), ResourceManager.getMessage("security.dialog.caption"));
      }
      catch (Exception localException)
      {
        Trace.securityPrintException(localException, ResourceManager.getMessage("rsa.general_error"), ResourceManager.getMessage("security.dialog.caption"));
      }
    if ((!localPermissionCollection.implies(new AWTPermission("accessClipboard"))) && (!((PermissionCollection)localObject1).implies(new AWTPermission("accessClipboard"))))
      ToolkitStore.get().getAppContext().put("UNTRUSTED_CLIPBOARD_ACCESS_KEY", Boolean.TRUE);
    localPermissionCollection.add(new SecureCookiePermission(SecureCookiePermission.getURLOriginString(paramCodeSource.getLocation())));
    return localPermissionCollection;
  }

  public void addLocalJar(URL paramURL)
  {
    addURL(paramURL);
  }

  private void addInnerJarURL(File paramFile)
    throws MalformedURLException
  {
    URL localURL = paramFile.toURI().toURL();
    addURL(localURL);
  }

  public void addJar(String paramString)
    throws IOException
  {
    if ((paramString.toUpperCase().endsWith(".JARJAR")) && (this.base.getProtocol().equalsIgnoreCase("file")))
    {
      File localFile = null;
      String str = this.base.toString() + paramString;
      if (!this.JARJARtoJAR.containsKey(str))
      {
        JarFile localJarFile = null;
        int i = 0;
        try
        {
          localJarFile = new JarFile(this.base.getPath() + paramString, true);
          Enumeration localEnumeration = localJarFile.entries();
          int j = 0;
          if (!localEnumeration.hasMoreElements())
            throw new IOException("Invalid jarjar file");
          JarEntry localJarEntry = null;
          while (localEnumeration.hasMoreElements())
          {
            localJarEntry = (JarEntry)localEnumeration.nextElement();
            if (!localJarEntry.toString().toUpperCase().startsWith("META-INF/"))
            {
              if (!localJarEntry.toString().toUpperCase().endsWith(".JAR"))
                throw new IOException("Invalid entry in jarjar file.");
              j++;
              if (j > 1)
                break;
            }
          }
          if (j > 1)
          {
            localJarEntry = null;
            throw new IOException("Multiple JAR files inside JARJAR file");
          }
          byte[] arrayOfByte = new byte[8192];
          BufferedInputStream localBufferedInputStream = null;
          BufferedOutputStream localBufferedOutputStream = null;
          FileOutputStream localFileOutputStream = null;
          InputStream localInputStream = localJarFile.getInputStream(localJarEntry);
          int k = 0;
          try
          {
            localFile = File.createTempFile(localJarEntry.toString().substring(0, localJarEntry.toString().lastIndexOf('.')), ".jar");
            Trace.msgPrintln("pluginclassloader.created_file", new Object[] { localFile.getPath() }, TraceLevel.BASIC);
            updateJarProgress(paramString);
            localBufferedInputStream = new BufferedInputStream(localInputStream);
            localFileOutputStream = new FileOutputStream(localFile);
            localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
            int m;
            while ((m = localBufferedInputStream.read(arrayOfByte, 0, arrayOfByte.length)) != -1)
              localBufferedOutputStream.write(arrayOfByte, 0, m);
            localBufferedOutputStream.flush();
            k = 1;
            this.JARJARtoJAR.put(str, localFile);
            try
            {
              addInnerJarURL(localFile);
            }
            catch (MalformedURLException localMalformedURLException2)
            {
              throw new IllegalArgumentException(paramString);
            }
          }
          finally
          {
            if (localBufferedInputStream != null)
              localBufferedInputStream.close();
            if (localBufferedOutputStream != null)
              localBufferedOutputStream.close();
            if (localFileOutputStream != null)
              localFileOutputStream.close();
            localBufferedInputStream = null;
            localBufferedOutputStream = null;
            localFileOutputStream = null;
            if (k == 0)
            {
              Trace.msgPrintln("pluginclassloader.empty_file", new Object[] { localFile.getName() }, TraceLevel.BASIC);
              if (localFile != null)
                localFile.delete();
            }
          }
        }
        finally
        {
          if (localJarFile != null)
            localJarFile.close();
        }
      }
      else
      {
        localFile = (File)this.JARJARtoJAR.get(str);
        if (localFile != null)
          try
          {
            addInnerJarURL(localFile);
          }
          catch (MalformedURLException localMalformedURLException1)
          {
            throw new IllegalArgumentException(paramString);
          }
        updateJarProgress(paramString);
      }
    }
    else
    {
      super.addJar(paramString);
      updateJarProgress(paramString);
    }
  }

  private void updateJarProgress(String paramString)
  {
    if (this.base.getProtocol().equalsIgnoreCase("file"))
      try
      {
        URL localURL = new URL(this.base, paramString);
        if (!URLUtil.checkTargetURL(this.base, localURL))
          throw new SecurityException("Permission denied: " + localURL);
        boolean bool = ProgressMonitor.get().shouldMeterInput(localURL, "GET");
        if (bool)
        {
          ProgressSource localProgressSource = new ProgressSource(localURL, "GET", 10000L);
          localProgressSource.beginTracking();
          localProgressSource.updateProgress(10000L, 10000L);
          localProgressSource.finishTracking();
          localProgressSource.close();
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
  }

  public void release(sun.awt.AppContext paramAppContext)
  {
    if (!this.JARJARtoJAR.isEmpty())
    {
      Trace.msgPrintln("pluginclassloader.deleting_files");
      localObject1 = this.JARJARtoJAR.keySet();
      localObject2 = ((Set)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = ((Iterator)localObject2).next();
        File localFile = (File)this.JARJARtoJAR.get(localObject3);
        if (localFile != null)
        {
          Trace.msgPrintln("pluginclassloader.file", new Object[] { localFile.getPath() }, TraceLevel.BASIC);
          localFile.delete();
        }
      }
      this.JARJARtoJAR.clear();
    }
    if (paramAppContext == null)
      return;
    Object localObject1 = (ActivatorSecurityManager)System.getSecurityManager();
    Object localObject2 = paramAppContext.getThreadGroup();
    ((ActivatorSecurityManager)localObject1).lockThreadGroup((ThreadGroup)localObject2);
    try
    {
      paramAppContext.dispose();
    }
    catch (IllegalThreadStateException localIllegalThreadStateException)
    {
    }
    catch (Throwable localThrowable)
    {
      Trace.printException(localThrowable);
    }
    finally
    {
      ((ActivatorSecurityManager)localObject1).unlockThreadGroup((ThreadGroup)localObject2);
    }
  }

  public sun.awt.AppContext resetAppContext()
  {
    return super.resetAppContext();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.security.PluginClassLoader
 * JD-Core Version:    0.6.2
 */