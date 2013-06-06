package com.sun.deploy.cache;

import com.sun.deploy.config.Config;
import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.JarUtil;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
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

class CachedJarFile extends JarFile
  implements ResourceObject
{
  private Reference manRef = null;
  private Reference signingDataRef;
  private final URL resourceURL;
  private final String resourceVersion;
  private final File indexFile;
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
      CachedJarFile localCachedJarFile = new CachedJarFile(new File(super.getName()), this.manRef, this.signingDataRef, this.resourceURL, this.resourceVersion, this.indexFile);
      MemoryCache.addResourceReference(localCachedJarFile, this.resourceURL.toString());
      return localCachedJarFile;
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
    }
    throw new CloneNotSupportedException();
  }

  protected CachedJarFile(CacheEntry paramCacheEntry)
    throws IOException
  {
    this(new File(paramCacheEntry.getResourceFilename()), new SoftReference(null), new SoftReference(null), new URL(paramCacheEntry.getURL()), paramCacheEntry.getVersion(), paramCacheEntry.getIndexFile());
  }

  private CachedJarFile(File paramFile1, Reference paramReference1, Reference paramReference2, URL paramURL, String paramString, File paramFile2)
    throws IOException
  {
    super(paramFile1, false);
    this.manRef = paramReference1;
    this.signingDataRef = paramReference2;
    this.resourceURL = paramURL;
    this.resourceVersion = paramString;
    this.indexFile = paramFile2;
    ensureAncestorKnowsAboutManifest(this);
  }

  private static void ensureAncestorKnowsAboutManifest(JarFile paramJarFile)
    throws IOException
  {
    if (!Config.isJavaVersionAtLeast16())
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          private final JarFile val$jar;

          public Object run()
            throws IOException
          {
            try
            {
              Field localField = Manifest.class.getDeclaredField("manLoaded");
              if (localField != null)
              {
                localField.setAccessible(true);
                localField.setBoolean(this.val$jar, true);
                return null;
              }
            }
            catch (Exception localException)
            {
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((IOException)localPrivilegedActionException.getException());
      }
  }

  public ZipEntry getEntry(String paramString)
  {
    ZipEntry localZipEntry = super.getEntry(paramString);
    if (localZipEntry != null)
      return new JarFileEntry(localZipEntry);
    return null;
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

  public void doClose()
    throws IOException
  {
    super.close();
  }

  private boolean isReferencedTo()
  {
    CacheEntry localCacheEntry = (CacheEntry)MemoryCache.getLoadedResource(this.resourceURL.toString(), false);
    return (localCacheEntry != null) && (this.indexFile != null) && (this.indexFile.equals(localCacheEntry.getIndexFile()));
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
          ZipEntry localZipEntry = (ZipEntry)this.val$entryList.nextElement();
          return new CachedJarFile.JarFileEntry(CachedJarFile.this, localZipEntry);
        }
        catch (InternalError localInternalError)
        {
        }
        throw new InternalError("Error in CachedJarFile entries");
      }
    };
  }

  public synchronized Manifest getManifest()
    throws IOException
  {
    if (this.manRef == null)
      return null;
    Manifest localManifest = (Manifest)this.manRef.get();
    if (localManifest == null)
    {
      CacheEntry localCacheEntry = getCacheEntry();
      if (localCacheEntry != null)
        localManifest = localCacheEntry.getManifest();
      else
        Trace.print("Warning: NULL cache entry for loaded resource!");
      if (localManifest != null)
        this.manRef = new SoftReference(localManifest);
      else
        this.manRef = null;
    }
    return localManifest;
  }

  public URL getResourceURL()
  {
    return this.resourceURL;
  }

  public String getResourceVersion()
  {
    return this.resourceVersion;
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
      localCacheEntry = recoverCacheEntry(this.indexFile, this.resourceURL, this.resourceVersion);
      if (localCacheEntry != null)
        clearReferences();
    }
    return localCacheEntry;
  }

  static CacheEntry recoverCacheEntry(File paramFile, URL paramURL, String paramString)
  {
    CacheEntry localCacheEntry = recoverOldCacheEntry(paramFile, paramURL);
    if (localCacheEntry != null)
    {
      MemoryCache.addLoadedResource(paramURL.toString(), localCacheEntry);
      return localCacheEntry;
    }
    return recoverCacheEntry(paramURL, paramString);
  }

  private static CacheEntry recoverOldCacheEntry(File paramFile, URL paramURL)
  {
    CacheEntry localCacheEntry = null;
    if ((paramFile != null) && (paramFile.isFile()))
      try
      {
        localCacheEntry = Cache.getCacheEntryFromFile(paramFile, true);
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
    if (localCacheEntry == null)
      Trace.println("Failed to recover old CacheEntry for " + paramURL + "from " + paramFile, TraceLevel.CACHE);
    else
      Trace.println("Recovered memory CacheEntry from: " + paramURL, TraceLevel.CACHE);
    return localCacheEntry;
  }

  static CacheEntry recoverCacheEntry(URL paramURL, String paramString)
  {
    CacheEntry localCacheEntry = Cache.getCacheEntry(paramURL, paramString);
    if (localCacheEntry != null)
      Trace.println("Recovered CacheEntry: " + localCacheEntry, TraceLevel.CACHE);
    else
      Trace.println("Failed to recover with latest CacheEntry", TraceLevel.CACHE);
    return localCacheEntry;
  }

  synchronized JarSigningData getSigningData()
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
    clear(new Reference[] { this.manRef, this.signingDataRef });
  }

  static void clear(Reference[] paramArrayOfReference)
  {
    if (paramArrayOfReference == null)
      return;
    for (int i = 0; i < paramArrayOfReference.length; i++)
      if (paramArrayOfReference[i] != null)
        paramArrayOfReference[i].clear();
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
    return localJarSigningData != null ? localJarSigningData.getCodeSources(paramURL) : new CodeSource[0];
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
      Manifest localManifest = CachedJarFile.this.getManifest();
      if (localManifest != null)
      {
        Attributes localAttributes = localManifest.getAttributes(getName());
        return localAttributes;
      }
      return null;
    }

    public Certificate[] getCertificates()
    {
      JarSigningData localJarSigningData = CachedJarFile.this.getSigningData();
      if (localJarSigningData != null)
        return localJarSigningData.getCertificates(getName());
      return null;
    }

    public CodeSigner[] getCodeSigners()
    {
      JarSigningData localJarSigningData = CachedJarFile.this.getSigningData();
      if (localJarSigningData != null)
        return localJarSigningData.getCodeSigners(getName());
      return null;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.CachedJarFile
 * JD-Core Version:    0.6.2
 */