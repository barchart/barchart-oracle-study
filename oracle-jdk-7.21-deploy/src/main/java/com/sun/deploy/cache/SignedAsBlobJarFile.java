package com.sun.deploy.cache;

import com.sun.deploy.config.Config;
import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.security.JarAsBLOBVerifier;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.JarUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class SignedAsBlobJarFile extends JarFile
  implements ResourceObject
{
  private final URL resourceURL;
  private final String resourceVersion;
  private final CodeSource codeSource;

  public SignedAsBlobJarFile(File paramFile, JarAsBLOBVerifier paramJarAsBLOBVerifier)
    throws IOException
  {
    super(paramFile);
    this.resourceURL = paramJarAsBLOBVerifier.getJarLocation();
    this.resourceVersion = paramJarAsBLOBVerifier.getJarVersion();
    this.codeSource = getCodeSource(paramJarAsBLOBVerifier);
    if (Cache.DEBUG)
      Trace.println("SignedAsBlobJarFile.const");
  }

  private SignedAsBlobJarFile(File paramFile, URL paramURL, String paramString, CodeSource paramCodeSource)
    throws IOException
  {
    super(paramFile);
    this.resourceURL = paramURL;
    this.resourceVersion = paramString;
    this.codeSource = paramCodeSource;
  }

  private static CodeSource getCodeSource(JarAsBLOBVerifier paramJarAsBLOBVerifier)
  {
    Map localMap = null;
    if (Config.isJavaVersionAtLeast15())
      localMap = paramJarAsBLOBVerifier.getCodeSourceCache();
    else
      throw new UnsupportedOperationException("Requires at least JRE 1.5");
    if ((localMap != null) && (localMap.size() == 1))
    {
      CodeSource localCodeSource = (CodeSource)localMap.values().iterator().next();
      if ((localCodeSource instanceof CodeSource))
        return cloneCodeSource((CodeSource)localCodeSource);
    }
    return getUnverifiedCodeSource(paramJarAsBLOBVerifier.getJarLocation());
  }

  Enumeration entryNames(CodeSource[] paramArrayOfCodeSource)
  {
    for (int i = 0; i < paramArrayOfCodeSource.length; i++)
      if (this.codeSource.equals(paramArrayOfCodeSource[i]))
      {
        Enumeration localEnumeration = super.entries();
        return new Enumeration()
        {
          private JarEntry next;
          private final Enumeration val$jarEntries;

          public boolean hasMoreElements()
          {
            if ((this.next == null) && (this.val$jarEntries.hasMoreElements()))
            {
              this.next = ((JarEntry)this.val$jarEntries.nextElement());
              if (this.next != null)
                for (String str = this.next.getName(); ((JarUtil.isSigningRelated(str)) || (str.endsWith("/"))) && (this.val$jarEntries.hasMoreElements()); str = this.next.getName())
                {
                  this.next = ((JarEntry)this.val$jarEntries.nextElement());
                  if (this.next == null)
                    break;
                }
            }
            return this.next != null;
          }

          public Object nextElement()
          {
            JarEntry localJarEntry = this.next;
            this.next = null;
            return localJarEntry.getName();
          }
        };
      }
    return Collections.emptyEnumeration();
  }

  private static CodeSource getUnverifiedCodeSource(URL paramURL)
  {
    return new CodeSource(paramURL, (Certificate[])null);
  }

  CodeSource[] getCodeSources(URL paramURL)
  {
    return new CodeSource[] { this.codeSource };
  }

  CodeSource getCodeSource(URL paramURL, String paramString)
  {
    if ((!JarUtil.isSigningRelated(paramString)) && (!paramString.endsWith("/")))
      return getCodeSources(paramURL)[0];
    return getUnverifiedCodeSource(paramURL);
  }

  public ZipEntry getEntry(String paramString)
  {
    JarEntry localJarEntry = (JarEntry)super.getEntry(paramString);
    if (localJarEntry == null)
      return null;
    return new BlobJarEntry(localJarEntry);
  }

  public void doClose()
    throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void close()
    throws IOException
  {
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      return new SignedAsBlobJarFile(new File(getName()), this.resourceURL, this.resourceVersion, cloneCodeSource(this.codeSource));
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    return null;
  }

  public URL getResourceURL()
  {
    return this.resourceURL;
  }

  public String getResourceVersion()
  {
    return this.resourceVersion;
  }

  public Enumeration entries()
  {
    Enumeration localEnumeration = super.entries();
    return new Enumeration()
    {
      private final Enumeration val$entryList;

      public boolean hasMoreElements()
      {
        return this.val$entryList.hasMoreElements();
      }

      public Object nextElement()
      {
        try
        {
          JarEntry localJarEntry = (JarEntry)this.val$entryList.nextElement();
          return new SignedAsBlobJarFile.BlobJarEntry(SignedAsBlobJarFile.this, localJarEntry);
        }
        catch (InternalError localInternalError)
        {
        }
        throw new InternalError("Error in CachedJarFile entries");
      }
    };
  }

  private static CodeSource cloneCodeSource(CodeSource paramCodeSource)
  {
    return new CodeSource(paramCodeSource.getLocation(), paramCodeSource.getCertificates());
  }

  private class BlobJarEntry extends JarEntry
  {
    public BlobJarEntry(JarEntry arg2)
    {
      super();
    }

    public Certificate[] getCertificates()
    {
      return SignedAsBlobJarFile.this.codeSource.getCertificates();
    }

    public CodeSigner[] getCodeSigners()
    {
      return SignedAsBlobJarFile.this.codeSource.getCodeSigners();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.SignedAsBlobJarFile
 * JD-Core Version:    0.6.2
 */