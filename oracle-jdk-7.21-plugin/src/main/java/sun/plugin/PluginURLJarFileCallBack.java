package sun.plugin;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.HttpUtils;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.Property;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.jnl.JARDesc;
import com.sun.jnlp.JNLPClassLoaderIf;
import com.sun.jnlp.JNLPClassLoaderUtil;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.jar.JarFile;
import sun.net.www.protocol.jar.URLJarFileCallBack;
import sun.plugin.cache.JarCacheUtil;

public class PluginURLJarFileCallBack
  implements URLJarFileCallBack
{
  private static int BUF_SIZE = 8192;

  private URLConnection connect(URL paramURL, boolean paramBoolean)
    throws IOException
  {
    URLConnection localURLConnection = paramURL.openConnection();
    if ((paramBoolean) && (!HttpUtils.hasGzipOrPack200Encoding(localURLConnection)))
      localURLConnection.setRequestProperty("accept-encoding", "pack200-gzip, gzip");
    localURLConnection.setRequestProperty("content-type", "application/x-java-archive");
    localURLConnection.connect();
    return localURLConnection;
  }

  private void downloadJAR(URLConnection paramURLConnection)
    throws IOException
  {
    Object localObject1 = null;
    try
    {
      localObject1 = paramURLConnection.getInputStream();
      if ((localObject1 != null) && (!(localObject1 instanceof FileInputStream)))
      {
        localObject1 = new BufferedInputStream((InputStream)localObject1);
        byte[] arrayOfByte = new byte[BUF_SIZE];
        while (((InputStream)localObject1).read(arrayOfByte) != -1);
      }
    }
    finally
    {
      if (localObject1 != null)
        ((InputStream)localObject1).close();
    }
  }

  public JarFile retrieve(final URL paramURL)
    throws IOException
  {
    JarFile localJarFile = null;
    final boolean bool = isPackEnable(paramURL);
    URL localURL = paramURL;
    if (bool)
      localURL = URLUtil.getPack200URL(paramURL, true);
    final URLConnection localURLConnection = connect(localURL, true);
    try
    {
      localJarFile = (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final URL val$url;
        private final URLConnection val$conn;
        private final boolean val$isPackEnabled;

        public Object run()
          throws IOException
        {
          URL localURL = new URL(paramURL.getProtocol(), paramURL.getHost(), paramURL.getPort(), paramURL.getPath());
          String str = (String)ToolkitStore.get().getAppContext().get("deploy-" + localURL.toString());
          JarFile localJarFile = null;
          Object localObject;
          if (ResourceProvider.get().canCache(localURL))
          {
            try
            {
              try
              {
                PluginURLJarFileCallBack.this.downloadJAR(localURLConnection);
              }
              catch (FileNotFoundException localFileNotFoundException)
              {
                if (bool)
                {
                  HttpUtils.cleanupConnection(localURLConnection);
                  URLUtil.clearPack200Original();
                  localObject = PluginURLJarFileCallBack.this.connect(paramURL, true);
                  PluginURLJarFileCallBack.this.downloadJAR((URLConnection)localObject);
                }
                else
                {
                  throw localFileNotFoundException;
                }
              }
            }
            catch (IOException localIOException)
            {
              Trace.ignoredException(localIOException);
              HttpUtils.cleanupConnection(localURLConnection);
              localObject = PluginURLJarFileCallBack.this.connect(paramURL, false);
              PluginURLJarFileCallBack.this.downloadJAR((URLConnection)localObject);
            }
            localJarFile = ResourceProvider.get().getCachedJarFile(str == null ? paramURL : localURL, str);
          }
          if (localJarFile == null)
          {
            int i = 256;
            if (bool)
              i |= 4096;
            localObject = ResourceProvider.get().getResource(str == null ? paramURL : localURL, str, true, i, null);
            localJarFile = localObject != null ? ((Resource)localObject).getJarFile() : null;
          }
          if (ResourceProvider.get().isInternalUse())
            return localJarFile;
          return JarCacheUtil.cloneJarFile(localJarFile);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
    finally
    {
      if (bool)
        URLUtil.clearPack200Original();
    }
    return localJarFile;
  }

  private static boolean isPackEnable(URL paramURL)
  {
    if (!Config.isJavaVersionAtLeast15())
      return false;
    try
    {
      JNLPClassLoaderIf localJNLPClassLoaderIf = JNLPClassLoaderUtil.getInstance();
      if (localJNLPClassLoaderIf != null)
      {
        JARDesc localJARDesc = localJNLPClassLoaderIf.getJarDescFromURL(paramURL);
        if (localJARDesc != null)
          return localJARDesc.isPack200Enabled();
      }
    }
    catch (Throwable localThrowable)
    {
    }
    return Property.isPackEnabled();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.PluginURLJarFileCallBack
 * JD-Core Version:    0.6.2
 */