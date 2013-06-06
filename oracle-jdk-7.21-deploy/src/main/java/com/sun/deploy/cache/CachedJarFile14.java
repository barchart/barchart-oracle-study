package com.sun.deploy.cache;

import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.JarUtil;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class CachedJarFile14 extends JarFile
  implements ResourceObject
{
  private Reference manifestRef = null;
  private Reference signingDataRef = null;
  private URL resourceURL;
  private String resourceVersion;
  private File indexFile;
  private static Enumeration emptyEnumeration = new Enumeration()
  {
    public boolean hasMoreElements()
    {
      return false;
    }

    public Object nextElement()
    {
      throw new NoSuchElementException();
    }
  };

  public String getName()
  {
    String str = super.getName();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null)
      return str;
    try
    {
      localSecurityManager.checkPermission(new RuntimePermission("accessDeploymentCache"));
      return str;
    }
    catch (SecurityException localSecurityException)
    {
    }
    return "";
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      CachedJarFile14 localCachedJarFile14 = new CachedJarFile14(new File(super.getName()), this.manifestRef, this.signingDataRef, this.resourceURL, this.resourceVersion, this.indexFile);
      MemoryCache.addResourceReference(localCachedJarFile14, this.resourceURL.toString());
      return localCachedJarFile14;
    }
    catch (IOException localIOException)
    {
    }
    throw new CloneNotSupportedException();
  }

  public URL getResourceURL()
  {
    return this.resourceURL;
  }

  public String getResourceVersion()
  {
    return this.resourceVersion;
  }

  private CachedJarFile14(File paramFile1, Reference paramReference1, Reference paramReference2, URL paramURL, String paramString, File paramFile2)
    throws IOException
  {
    super(paramFile1, false);
    this.manifestRef = paramReference1;
    this.signingDataRef = paramReference2;
    this.resourceURL = paramURL;
    this.resourceVersion = paramString;
    this.indexFile = paramFile2;
  }

  protected CachedJarFile14(CacheEntry paramCacheEntry)
    throws IOException
  {
    this(new File(paramCacheEntry.getResourceFilename()), new SoftReference(null), new SoftReference(null), new URL(paramCacheEntry.getURL()), paramCacheEntry.getVersion(), paramCacheEntry.getIndexFile());
  }

  public void doClose()
    throws IOException
  {
    super.close();
  }

  public void close()
    throws IOException
  {
    if (!isReferencedTo())
    {
      Trace.println("Closing CachedJarFile " + getName(), TraceLevel.CACHE);
      super.close();
    }
  }

  private boolean isReferencedTo()
  {
    CacheEntry localCacheEntry = (CacheEntry)MemoryCache.getLoadedResource(this.resourceURL.toString(), false);
    return (localCacheEntry != null) && (this.indexFile != null) && (this.indexFile.equals(localCacheEntry.getIndexFile()));
  }

  public ZipEntry getEntry(String paramString)
  {
    ZipEntry localZipEntry = super.getEntry(paramString);
    if (localZipEntry != null)
      return new JarFileEntry(localZipEntry);
    return null;
  }

  public Enumeration entries()
  {
    Enumeration localEnumeration = super.entries();
    return new Enumeration()
    {
      private final Enumeration val$enum14;

      public boolean hasMoreElements()
      {
        return this.val$enum14.hasMoreElements();
      }

      public Object nextElement()
      {
        try
        {
          ZipEntry localZipEntry = (ZipEntry)this.val$enum14.nextElement();
          return new CachedJarFile14.JarFileEntry(CachedJarFile14.this, localZipEntry);
        }
        catch (InternalError localInternalError)
        {
        }
        throw new InternalError("Error in CachedJarFile entries");
      }
    };
  }

  private synchronized CacheEntry getCacheEntry()
  {
    if (this.resourceURL == null)
      return null;
    CacheEntry localCacheEntry = (CacheEntry)MemoryCache.getLoadedResource(this.resourceURL.toString());
    if ((localCacheEntry == null) || (!this.indexFile.equals(localCacheEntry.getIndexFile())))
    {
      String str = "CachedJarFile.getCacheEntry: " + this.indexFile + " != " + localCacheEntry.getIndexFile() + " for " + this.resourceURL;
      Trace.println(str, TraceLevel.CACHE);
      localCacheEntry = CachedJarFile.recoverCacheEntry(this.indexFile, this.resourceURL, this.resourceVersion);
      if (localCacheEntry != null)
        clearReferences();
    }
    return localCacheEntry;
  }

  public synchronized Manifest getManifest()
    throws IOException
  {
    if (this.manifestRef == null)
      return null;
    Manifest localManifest = (Manifest)this.manifestRef.get();
    if (localManifest == null)
    {
      CacheEntry localCacheEntry = getCacheEntry();
      if (localCacheEntry != null)
        localManifest = localCacheEntry.getManifest();
      else
        Trace.println("Warning: NULL cache entry for loaded resource!", TraceLevel.CACHE);
      if (localManifest != null)
        this.manifestRef = new SoftReference(localManifest);
      else
        this.manifestRef = null;
    }
    return localManifest;
  }

  private synchronized JarSigningData getSigningData()
  {
    if (this.signingDataRef == null)
      return null;
    JarSigningData localJarSigningData = (JarSigningData)this.signingDataRef.get();
    if (localJarSigningData == null)
    {
      CacheEntry localCacheEntry = getCacheEntry();
      if (localCacheEntry != null)
      {
        localJarSigningData = localCacheEntry.getJarSigningData();
        if (localJarSigningData != null)
          this.signingDataRef = new SoftReference(localJarSigningData);
        else
          this.signingDataRef = null;
      }
      else
      {
        Trace.println("getSignerMap failed to get CacheEntry for " + this.resourceURL, TraceLevel.CACHE);
      }
    }
    return localJarSigningData;
  }

  private synchronized void clearReferences()
  {
    CachedJarFile.clear(new Reference[] { this.manifestRef, this.signingDataRef });
  }

  Enumeration entryNames(CodeSource[] paramArrayOfCodeSource)
  {
    JarSigningData localJarSigningData = getSigningData();
    if ((localJarSigningData != null) && (localJarSigningData.matchStrictSingleSigning(paramArrayOfCodeSource)))
      return unsignedEntryNames(null);
    boolean bool = false;
    ArrayList localArrayList = new ArrayList();
    if (localJarSigningData != null)
      bool = localJarSigningData.collectEntryNamesBySigners(paramArrayOfCodeSource, localArrayList);
    Map localMap = localJarSigningData != null ? localJarSigningData.getSignerMap() : null;
    Iterator localIterator = localArrayList.iterator();
    Enumeration localEnumeration = bool ? unsignedEntryNames(localMap) : emptyEnumeration;
    return new Enumeration()
    {
      String name;
      private final Iterator val$signerKeys;
      private final Enumeration val$enum2;

      public boolean hasMoreElements()
      {
        if (this.name != null)
          return true;
        if (this.val$signerKeys.hasNext())
        {
          this.name = ((String)this.val$signerKeys.next());
          return true;
        }
        if (this.val$enum2.hasMoreElements())
        {
          this.name = ((String)this.val$enum2.nextElement());
          return true;
        }
        return false;
      }

      public Object nextElement()
      {
        if (hasMoreElements())
        {
          String str = this.name;
          this.name = null;
          return str;
        }
        throw new NoSuchElementException();
      }
    };
  }

  private Enumeration unsignedEntryNames(Map paramMap)
  {
    Enumeration localEnumeration = entries();
    return new Enumeration()
    {
      String name;
      private final Enumeration val$entries;
      private final Map val$signerMap;

      public boolean hasMoreElements()
      {
        if (this.name != null)
          return true;
        while (this.val$entries.hasMoreElements())
        {
          ZipEntry localZipEntry = (ZipEntry)this.val$entries.nextElement();
          String str = localZipEntry.getName();
          if ((!localZipEntry.isDirectory()) && (!JarUtil.isSigningRelated(str)))
            if ((this.val$signerMap == null) || (this.val$signerMap.get(str) == null))
            {
              this.name = str;
              return true;
            }
        }
        return false;
      }

      public Object nextElement()
      {
        if (hasMoreElements())
        {
          String str = this.name;
          this.name = null;
          return str;
        }
        throw new NoSuchElementException();
      }
    };
  }

  synchronized CodeSource[] getCodeSources(URL paramURL)
  {
    JarSigningData localJarSigningData = getSigningData();
    return localJarSigningData != null ? localJarSigningData.getCodeSources(paramURL) : null;
  }

  synchronized CodeSource getCodeSource(URL paramURL, String paramString)
  {
    JarSigningData localJarSigningData = getSigningData();
    return localJarSigningData != null ? localJarSigningData.getCodeSource(paramURL, paramString) : null;
  }

  private class JarFileEntry extends JarEntry
  {
    JarFileEntry(ZipEntry arg2)
    {
      super();
    }

    public Attributes getAttributes()
      throws IOException
    {
      Manifest localManifest = CachedJarFile14.this.getManifest();
      if (localManifest != null)
        return localManifest.getAttributes(getName());
      return null;
    }

    public Certificate[] getCertificates()
    {
      return CachedJarFile14.this.getSigningData().getCertificates(getName());
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.CachedJarFile14
 * JD-Core Version:    0.6.2
 */