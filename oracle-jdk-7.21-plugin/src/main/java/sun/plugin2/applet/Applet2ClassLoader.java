package sun.plugin2.applet;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.JARSigningException;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.URLUtil;
import com.sun.jnlp.JNLPPreverifyClassLoader.DelegatingThread;
import com.sun.jnlp.JNLPPreverifyClassLoader.UndelegatingThread;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import sun.net.www.ParseUtil;
import sun.plugin2.util.SystemUtil;

public class Applet2ClassLoader extends Plugin2ClassLoader
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private Object syncResourceAsStream = new Object();
  private Object syncResourceAsStreamFromJar = new Object();
  private boolean resourceAsStreamInCall = false;
  private boolean resourceAsStreamFromJarInCall = false;
  private boolean processingException = false;

  private Applet2ClassLoader(URL paramURL)
  {
    super(new URL[0], paramURL);
  }

  private Applet2ClassLoader(URL paramURL, ClassLoader paramClassLoader)
  {
    super(new URL[0], paramURL, paramClassLoader);
  }

  public static Applet2ClassLoader newInstance(URL paramURL)
  {
    Applet2ClassLoader localApplet2ClassLoader1 = new Applet2ClassLoader(paramURL);
    if (Config.getMixcodeValue() != 3)
    {
      Applet2ClassLoader localApplet2ClassLoader2 = new Applet2ClassLoader(paramURL, localApplet2ClassLoader1);
      if (!Plugin2ClassLoader.setDeployURLClassPathCallbacks(localApplet2ClassLoader1, localApplet2ClassLoader2))
        return localApplet2ClassLoader1;
      localApplet2ClassLoader1._delegatingClassLoader = localApplet2ClassLoader2;
      return localApplet2ClassLoader2;
    }
    return localApplet2ClassLoader1;
  }

  private int getPermissionRequestType()
  {
    Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
    if ((localPlugin2Manager instanceof Applet2Manager))
      return ((Applet2Manager)localPlugin2Manager).getPermissionRequestType();
    return 0;
  }

  public boolean wantsAllPerms(CodeSource paramCodeSource)
  {
    int i = getPermissionRequestType();
    if (i == 2)
      return true;
    if (i == 1)
      return false;
    return (paramCodeSource != null) && (paramCodeSource.getCertificates() != null);
  }

  public URL[] getURLs()
  {
    URL[] arrayOfURL1 = super.getURLs();
    URL[] arrayOfURL2 = new URL[arrayOfURL1.length + 1];
    System.arraycopy(arrayOfURL1, 0, arrayOfURL2, 0, arrayOfURL1.length);
    arrayOfURL2[(arrayOfURL2.length - 1)] = this.base;
    return arrayOfURL2;
  }

  public String toString()
  {
    return "Applet2ClassLoader{" + Arrays.asList(getURLs()) + '}';
  }

  protected Class findClass(String paramString)
    throws ClassNotFoundException
  {
    return findClass(paramString, false);
  }

  protected Class findClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    try
    {
      return findClassHelper(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      if ((localClassNotFoundException1.getCause() instanceof JARSigningException))
        throw localClassNotFoundException1;
      Object localObject5;
      synchronized (this)
      {
        if ((!paramBoolean) && (!this.processingException) && (this._delegatingClassLoader != null) && (needToApplyWorkaround()))
        {
          this.processingException = true;
          try
          {
            int i = 0;
            localObject1 = new DelegatingThread(this._delegatingClassLoader, this);
            ((DelegatingThread)localObject1).start();
            while (!((DelegatingThread)localObject1).done())
              try
              {
                wait();
              }
              catch (InterruptedException localInterruptedException1)
              {
                i = 1;
              }
            if (i == 0)
            {
              localObject2 = this._delegatingClassLoader.loadClass(paramString);
              jsr 23;
              return localObject2;
            }
          }
          finally
          {
            jsr 6;
          }
          localObject4 = returnAddress;
          int j = 0;
          localObject5 = new UndelegatingThread(this._delegatingClassLoader, this);
          ((UndelegatingThread)localObject5).start();
          while (!((UndelegatingThread)localObject5).done())
            try
            {
              wait();
            }
            catch (InterruptedException localInterruptedException2)
            {
              j = 1;
            }
          if (j == 0)
            this.processingException = false;
          ret;
        }
      }
      ClassNotFoundException localClassNotFoundException2 = (ClassNotFoundException)cnfeThreadLocal.get();
      if (localClassNotFoundException2 != null)
        cnfeThreadLocal.set(null);
      if (!getCodebaseLookup())
      {
        if (localClassNotFoundException2 != null)
          throw localClassNotFoundException2;
        throw new ClassNotFoundException(paramString);
      }
      ??? = new CodeSource[1];
      String str1 = parseUtilEncodePath(paramString.replace('.', '/'), false);
      Object localObject1 = getAppContext();
      Object localObject2 = null;
      String str2 = "";
      if (localObject1 != null)
      {
        localObject2 = ((AppContext)localObject1).get("applet-code-rewrite-" + paramString);
        if (localObject2 != null)
          str2 = localObject2.toString();
      }
      final String str3 = str1 + ".class" + str2;
      try
      {
        byte[] arrayOfByte = (byte[])AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          private final String val$path;
          private final CodeSource[] val$actualCodeSource;

          public Object run()
            throws IOException, SecurityException
          {
            try
            {
              URL localURL = new URL(Applet2ClassLoader.this.base, str3);
              if (!URLUtil.checkTargetURL(Plugin2Manager.getCurrentManager().getDocumentBase(), localURL))
                throw new SecurityException("Permission denied: " + localURL);
              if (!URLUtil.checkTargetURL(Applet2ClassLoader.this.base, localURL))
                throw new SecurityException("Permission denied: " + localURL);
              if ((Applet2ClassLoader.this.base.getProtocol().equals(localURL.getProtocol())) && (Applet2ClassLoader.this.base.getHost().equals(localURL.getHost())) && (Applet2ClassLoader.this.base.getPort() == localURL.getPort()))
                return Applet2ClassLoader.getBytes(localURL, this.val$actualCodeSource);
              return null;
            }
            catch (Exception localException)
            {
              if ((localException instanceof SecurityException))
                throw ((SecurityException)localException);
            }
            return null;
          }
        }
        , this._acc);
        if (arrayOfByte != null)
        {
          localObject5 = ???[0] != null ? ???[0] : this.codesource;
          try
          {
            if ((getSecurityCheck()) && (TrustDecider.isAllPermissionGranted((CodeSource)localObject5, getPreloader()) == 0L))
              throw new ClassNotFoundException(paramString);
          }
          catch (Exception localException)
          {
            throw newClassNotFoundException(paramString);
          }
          checkResource(paramString.replace('.', '/') + ".class");
          return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, (CodeSource)localObject5);
        }
        if (localClassNotFoundException2 != null)
          throw localClassNotFoundException2;
        throw new ClassNotFoundException(paramString);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        if (localClassNotFoundException2 != null)
          throw localClassNotFoundException2;
        throw new ClassNotFoundException(paramString, localPrivilegedActionException.getException());
      }
    }
  }

  public InputStream getResourceAsStream(String paramString)
  {
    if (paramString == null)
      throw new NullPointerException("name");
    try
    {
      InputStream localInputStream = null;
      synchronized (this.syncResourceAsStream)
      {
        this.resourceAsStreamInCall = true;
        localInputStream = super.getResourceAsStream(paramString);
        this.resourceAsStreamInCall = false;
      }
      if ((localInputStream == null) && (getCodebaseLookup() == true))
      {
        ??? = new URL(this.base, parseUtilEncodePath(paramString, false));
        if (!URLUtil.checkTargetURL(this.base, (URL)???))
          throw new SecurityException("Permission denied: " + ???);
        localInputStream = ((URL)???).openStream();
        checkResource(paramString);
      }
      return localInputStream;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public URL findResource(String paramString)
  {
    URL localURL = super.findResource(paramString);
    if (paramString.startsWith("META-INF/"))
      return localURL;
    if (localURL == null)
    {
      if (!getCodebaseLookup())
        return localURL;
      boolean bool1 = false;
      synchronized (this.syncResourceAsStreamFromJar)
      {
        bool1 = this.resourceAsStreamFromJarInCall;
      }
      if (bool1)
        return null;
      boolean bool2 = false;
      synchronized (this.syncResourceAsStream)
      {
        bool2 = this.resourceAsStreamInCall;
      }
      if (!bool2)
        try
        {
          localURL = new URL(this.base, parseUtilEncodePath(paramString, false));
          if (!URLUtil.checkTargetURL(this.base, localURL))
            throw new SecurityException("Permission denied: " + localURL);
          if (!resourceExists(localURL))
            localURL = null;
          else
            checkResource(paramString);
        }
        catch (Exception localException)
        {
          localURL = null;
        }
    }
    return localURL;
  }

  public Enumeration findResources(String paramString)
    throws IOException
  {
    final Enumeration localEnumeration = super.findResources(paramString);
    if (paramString.startsWith("META-INF/"))
      return localEnumeration;
    if (!getCodebaseLookup())
      return localEnumeration;
    URL localURL1 = new URL(this.base, parseUtilEncodePath(paramString, false));
    if (!URLUtil.checkTargetURL(this.base, localURL1))
      throw new SecurityException("Permission denied: " + localURL1);
    if (!resourceExists(localURL1))
      localURL1 = null;
    else
      try
      {
        checkResource(paramString);
      }
      catch (Exception localException)
      {
        localURL1 = null;
      }
    final URL localURL2 = localURL1;
    return new Enumeration()
    {
      private boolean done;
      private final Enumeration val$e;
      private final URL val$url;

      public Object nextElement()
      {
        if (!this.done)
        {
          if (localEnumeration.hasMoreElements())
            return localEnumeration.nextElement();
          this.done = true;
          if (localURL2 != null)
            return localURL2;
        }
        throw new NoSuchElementException();
      }

      public boolean hasMoreElements()
      {
        return (!this.done) && ((localEnumeration.hasMoreElements()) || (localURL2 != null));
      }
    };
  }

  protected void addJar(String paramString)
    throws IOException
  {
    if ((paramString == null) || (paramString.equals("")))
      return;
    if (DEBUG)
      System.out.println("Applet2ClassLoader: addJar:  base " + this.base + " jar: " + paramString);
    URL localURL;
    try
    {
      localURL = new URL(this.base, paramString);
      if (!URLUtil.checkTargetURL(this.base, localURL))
        throw new SecurityException("Permission denied: " + localURL);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new IllegalArgumentException("name");
    }
    addURL(localURL);
  }

  void addLocalJar(URL paramURL)
  {
    addURL(paramURL, true);
  }

  public URL getBaseURL()
  {
    return this.base;
  }

  public InputStream getResourceAsStreamFromJar(String paramString)
  {
    if (paramString == null)
      throw new NullPointerException("name");
    try
    {
      InputStream localInputStream = null;
      synchronized (this.syncResourceAsStreamFromJar)
      {
        this.resourceAsStreamFromJarInCall = true;
        localInputStream = super.getResourceAsStream(paramString);
        this.resourceAsStreamFromJarInCall = false;
      }
      return localInputStream;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  private boolean resourceExists(URL paramURL)
  {
    boolean bool = true;
    try
    {
      URLConnection localURLConnection = paramURL.openConnection();
      Object localObject;
      if ((localURLConnection instanceof HttpURLConnection))
      {
        localObject = (HttpURLConnection)localURLConnection;
        ((HttpURLConnection)localObject).setRequestMethod("HEAD");
        int i = ((HttpURLConnection)localObject).getResponseCode();
        if (i == 200)
          return true;
        if (i >= 400)
          return false;
      }
      else
      {
        localObject = localURLConnection.getInputStream();
        ((InputStream)localObject).close();
      }
    }
    catch (Exception localException)
    {
      bool = false;
    }
    return bool;
  }

  private static byte[] getBytes(URL paramURL, CodeSource[] paramArrayOfCodeSource)
    throws IOException
  {
    URLConnection localURLConnection = paramURL.openConnection();
    if ((localURLConnection instanceof HttpURLConnection))
    {
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURLConnection;
      int j = localHttpURLConnection.getResponseCode();
      if (j >= 400)
        throw new IOException("open HTTP connection failed:" + paramURL);
    }
    int i = localURLConnection.getContentLength();
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream());
    Object localObject1;
    Object localObject3;
    try
    {
      if (i != -1)
      {
        localObject1 = new byte[i];
        while (i > 0)
        {
          k = localBufferedInputStream.read((byte[])localObject1, localObject1.length - i, i);
          if (k == -1)
            throw new IOException("unexpected EOF");
          i -= k;
        }
      }
      localObject1 = new byte[8192];
      int k = 0;
      while ((i = localBufferedInputStream.read((byte[])localObject1, k, localObject1.length - k)) != -1)
      {
        k += i;
        if (k >= localObject1.length)
        {
          localObject3 = new byte[k * 2];
          System.arraycopy(localObject1, 0, localObject3, 0, k);
          localObject1 = localObject3;
        }
      }
      if (k != localObject1.length)
      {
        localObject3 = new byte[k];
        System.arraycopy(localObject1, 0, localObject3, 0, k);
        localObject1 = localObject3;
      }
    }
    finally
    {
      localBufferedInputStream.close();
    }
    if (localObject1 != null)
    {
      Object localObject2 = localURLConnection.getURL();
      localObject3 = (String)ToolkitStore.get().getAppContext().get("deploy-" + paramURL.toString());
      Resource localResource = ResourceProvider.get().getCachedResource(paramURL, (String)localObject3);
      Object localObject6;
      if (localResource != null)
      {
        localObject6 = new URL(localResource.getURL());
        if (!URLUtil.sameURLs((URL)localObject6, paramURL))
          localObject2 = localObject6;
      }
      if (!URLUtil.sameURLs((URL)localObject2, paramURL))
      {
        localObject6 = new CodeSource((URL)localObject2, (Certificate[])null);
        paramArrayOfCodeSource[0] = localObject6;
      }
    }
    return localObject1;
  }

  private String parseUtilEncodePath(String paramString, boolean paramBoolean)
  {
    try
    {
      return ParseUtil.encodePath(paramString, paramBoolean);
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
    }
    return ParseUtil.encodePath(paramString);
  }

  static class DelegatingThread extends JNLPPreverifyClassLoader.DelegatingThread
  {
    DelegatingThread(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
    {
      super(paramClassLoader2);
    }

    protected void quiesce(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
    {
      boolean bool = this.waiter.equals(paramClassLoader1);
      if ((paramClassLoader1 instanceof Applet2ClassLoader))
        ((Applet2ClassLoader)paramClassLoader1).quiescenceRequested(this.thread, bool);
      if (bool)
        return;
      quiesce(paramClassLoader1.getParent(), paramClassLoader2);
    }
  }

  public static class UndelegatingThread extends JNLPPreverifyClassLoader.UndelegatingThread
  {
    public UndelegatingThread(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
    {
      super(paramClassLoader2);
    }

    protected void unquiesce(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
    {
      boolean bool = paramClassLoader2.equals(paramClassLoader1);
      if (!bool)
        unquiesce(paramClassLoader1.getParent(), paramClassLoader2);
      if ((paramClassLoader1 instanceof Applet2ClassLoader))
        ((Applet2ClassLoader)paramClassLoader1).quiescenceCancelled(bool);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2ClassLoader
 * JD-Core Version:    0.6.2
 */