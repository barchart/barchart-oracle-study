package com.sun.jnlp;

import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.BasicHttpRequest;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.net.protocol.jar.Handler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.net.www.protocol.jar.JarURLConnection;

public class JNLPCachedJarURLConnection extends JarURLConnection
{
  private URL _jarFileURL = null;
  private String _entryName;
  private JarEntry _jarEntry;
  private JarFile _jarFile;
  private String _contentType;
  private boolean _useCachedJar = false;
  private Map headerFields = null;

  public JNLPCachedJarURLConnection(URL paramURL, Handler paramHandler)
    throws MalformedURLException, IOException
  {
    super(paramURL, paramHandler);
    getJarFileURL();
    this._entryName = getEntryName();
  }

  public String getHeaderField(String paramString)
  {
    if (paramString == null)
      return null;
    try
    {
      connect();
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
    }
    if ((this.headerFields != null) && (BasicHttpRequest.isHeaderFieldCached(paramString)))
    {
      List localList = (List)this.headerFields.get(paramString.toLowerCase());
      if (localList != null)
        return (String)localList.get(0);
      return null;
    }
    return super.getHeaderField(paramString);
  }

  public synchronized URL getJarFileURL()
  {
    if ((this._jarFile instanceof ResourceObject))
    {
      URL localURL = ((ResourceObject)this._jarFile).getResourceURL();
      if (localURL != null)
        return localURL;
    }
    return getJarFileURLInternal();
  }

  private URL getJarFileURLInternal()
  {
    if (this._jarFileURL == null)
      this._jarFileURL = super.getJarFileURL();
    return this._jarFileURL;
  }

  public JarFile getJarFile()
    throws IOException
  {
    connect();
    if (ResourceProvider.get().isInternalUse())
      return this._jarFile;
    try
    {
      return (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          if ((JNLPCachedJarURLConnection.this._jarFile instanceof ResourceObject))
            return ((ResourceObject)JNLPCachedJarURLConnection.this._jarFile).clone();
          String str = JNLPCachedJarURLConnection.this._jarFile.getName();
          if (new File(str).exists())
          {
            JarFile localJarFile = new JarFile(str);
            Manifest localManifest = localJarFile.getManifest();
            if (localManifest != null)
              localManifest.getMainAttributes().remove(Attributes.Name.CLASS_PATH);
            return localJarFile;
          }
          return JNLPCachedJarURLConnection.this._jarFile;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new IOException(localPrivilegedActionException.getCause().getMessage());
    }
  }

  private JarFile getJarFileInternal()
    throws IOException
  {
    connect();
    return this._jarFile;
  }

  public JarEntry getJarEntry()
    throws IOException
  {
    connect();
    return this._jarEntry;
  }

  public void connect()
    throws IOException
  {
    if (!this.connected)
    {
      this._jarFile = JNLPClassLoaderUtil.getInstance().getJarFile(this._jarFileURL);
      if (this._jarFile != null)
      {
        this._useCachedJar = true;
      }
      else
      {
        super.connect();
        this._jarFile = super.getJarFile();
      }
      JARDesc localJARDesc = JNLPClassLoaderUtil.getInstance().getJarDescFromURL(this._jarFileURL);
      if (localJARDesc != null)
      {
        Resource localResource = ResourceProvider.get().getCachedResource(localJARDesc.getLocation(), localJARDesc.getVersion());
        Object localObject = localResource != null ? localResource.getHeaders() : null;
        if (localObject != null)
        {
          this.headerFields = new HashMap();
          this.headerFields = localObject;
        }
      }
      if (this._entryName != null)
      {
        this._jarEntry = this._jarFile.getJarEntry(this._entryName);
        if (this._jarEntry == null)
          throw new FileNotFoundException("JAR entry " + this._entryName + " not found in " + this._jarFile.getName());
      }
      this.connected = true;
    }
  }

  public InputStream getInputStream()
    throws IOException
  {
    connect();
    if (this._useCachedJar)
    {
      Object localObject = null;
      if (this._entryName == null)
        throw new IOException("no entry name specified");
      if (this._jarEntry == null)
        throw new FileNotFoundException("JAR entry " + this._entryName + " not found in " + this._jarFile.getName());
      return this._jarFile.getInputStream(this._jarEntry);
    }
    return super.getInputStream();
  }

  public Object getContent()
    throws IOException
  {
    connect();
    if ((this._useCachedJar) && (this._entryName == null))
      return getJarFile();
    return super.getContent();
  }

  public String getContentType()
  {
    try
    {
      connect();
    }
    catch (IOException localIOException1)
    {
    }
    if (this._useCachedJar)
    {
      if (this._contentType == null)
      {
        if (this._entryName == null)
          this._contentType = "x-java/jar";
        else
          try
          {
            connect();
            InputStream localInputStream = getJarFileInternal().getInputStream(this._jarEntry);
            this._contentType = guessContentTypeFromStream(new BufferedInputStream(localInputStream));
            localInputStream.close();
          }
          catch (IOException localIOException2)
          {
          }
        if (this._contentType == null)
          this._contentType = guessContentTypeFromName(this._entryName);
        if (this._contentType == null)
          this._contentType = "content/unknown";
      }
    }
    else
      this._contentType = super.getContentType();
    return this._contentType;
  }

  public int getContentLength()
  {
    try
    {
      connect();
    }
    catch (IOException localIOException)
    {
      return super.getContentLength();
    }
    if (this._useCachedJar)
    {
      if (this._jarEntry != null)
        return (int)this._jarEntry.getSize();
      return -1;
    }
    return super.getContentLength();
  }

  public URL getURL()
  {
    URL localURL = getJarFileURL();
    if (localURL != null)
      try
      {
        return URLUtil.getJarEntryURL(localURL, this._entryName);
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    return super.getURL();
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.jnlp.JNLPCachedJarURLConnection
 * JD-Core Version:    0.6.2
 */