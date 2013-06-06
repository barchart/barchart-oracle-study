package com.sun.deploy.cache;

import com.sun.deploy.Environment;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.DownloadDelegate;
import com.sun.deploy.model.Resource;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.net.HttpUtils;
import com.sun.deploy.net.JARSigningException;
import com.sun.deploy.net.MessageHeader;
import com.sun.deploy.net.UpdateTracker;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.JarVerifier;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.BlackList;
import com.sun.deploy.util.SyncFileAccess;
import com.sun.deploy.util.SyncFileAccess.RandomAccessFileLock;
import com.sun.deploy.util.TrustedLibraries;
import com.sun.deploy.util.VersionString;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SignatureException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipException;
import sun.misc.JavaUtilJarAccess;
import sun.misc.SharedSecrets;
import sun.security.pkcs.ParsingException;

public class CacheEntry
  implements Resource
{
  private static final String ABSOLUTE_PATH_ESCAPE_CHAR = "\t";
  public static int INCOMPLETE_FALSE = 0;
  public static int INCOMPLETE_TRUE = 1;
  public static int INCOMPLETE_ONHOLD = 2;
  public static int BUSY_FALSE = 0;
  public static int BUSY_TRUE = 1;
  private File indexFile = null;
  private SyncFileAccess indexFileSyncAccess = null;
  private File tempDataFile = null;
  private int busy = BUSY_TRUE;
  private int incomplete = 0;
  private int forceUpdate = 0;
  private int cacheVersion = Cache.getCacheVersion();
  private int contentLength = 0;
  private int isShortcutImage = 0;
  private int isProxiedHost = 0;
  private long lastModified = 0L;
  private long expirationDate = 0L;
  private String version = null;
  private String url = "";
  private String namespaceID = "";
  private MessageHeader headerFields = new MessageHeader();
  private String filename = null;
  private String codebaseIP = null;
  private long validationTimestampt = 0L;
  private long certExpirationDate = 0L;
  private boolean knownToBeSigned = false;
  private long blacklistValidationTime = 0L;
  private long trustedLibrariesValidationTime = 0L;
  private byte classVerificationStatus = 0;
  Map checkedJars = null;
  private boolean hasOnlySignedEntries = false;
  private boolean hasSingleCodeSource = false;
  private boolean hasMissingSignedEntries = false;
  private static final int section1Length = 128;
  private int section2Length = 0;
  private int section3Length = 0;
  private int section4Length = 0;
  private int section4CertsLength = 0;
  private int section4SignersLength = 0;
  private int section4Pre15Length = 0;
  private int section5Length = 0;
  private int reducedManifestLength = 0;
  private int reducedManifest2Length = 0;
  private SoftReference manifestRef = null;
  private boolean doneReadManifest = false;
  private SoftReference signingDataRef = new SoftReference(null);
  private WeakReference jarRef = new WeakReference(null);
  private static boolean enhancedJarAccess = false;
  private volatile URL _url;

  static boolean hasEnhancedJarAccess()
  {
    return enhancedJarAccess;
  }

  void verifyJAR(URLClassLoader paramURLClassLoader)
  {
    JarFile localJarFile = getJarFile();
    if (localJarFile == null)
      return;
    Enumeration localEnumeration = localJarFile.entries();
    int i = 0;
    int j = 0;
    while (localEnumeration.hasMoreElements())
    {
      JarEntry localJarEntry = (JarEntry)localEnumeration.nextElement();
      String str1 = localJarEntry.getName();
      if ((str1 != null) && (str1.endsWith(".class")))
        try
        {
          String str2 = str1.substring(0, str1.lastIndexOf(".class"));
          Class localClass = Class.forName(str2.replace('/', '.'), false, paramURLClassLoader);
          i = 1;
        }
        catch (Throwable localThrowable)
        {
          int k = 0;
          String str3 = localThrowable.getMessage() != null ? localThrowable.getMessage().replace('/', '.') : localThrowable.getClass().getName();
          if ((str3 != null) && ((str3.indexOf("com.sun.media.jmcimpl.JMFPlayerPeer") != -1) || (str3.indexOf("javafx.fxunit.FXTestCase") != -1) || (str3.indexOf("javax.media.ControllerListener") != -1) || (str3.indexOf("junit.framework.TestCase") != -1)))
            try
            {
              URL localURL = new URL(getURL());
              String str4 = localURL.getHost();
              if (str4.equals("dl.javafx.com"))
              {
                Trace.println("CacheEntry:  Skipped verification for class " + str3 + " in " + getURL(), TraceLevel.CACHE);
                k = 1;
              }
            }
            catch (Exception localException)
            {
            }
          if (Environment.allowAltJavaFxRuntimeURL())
          {
            k = 1;
            if (Trace.isEnabled(TraceLevel.CACHE))
              localThrowable.printStackTrace();
          }
          if (k == 0)
          {
            Trace.println("Class verification failed: " + localThrowable.getMessage() + " for " + getURL(), TraceLevel.CACHE);
            j = 1;
            break;
          }
        }
    }
    if ((i != 0) && (j == 0))
    {
      updateClassVerificationStatus((byte)1);
      Trace.println("CacheEntry:  Pre-verify done for all classes in " + getURL(), TraceLevel.CACHE);
      return;
    }
    updateClassVerificationStatus((byte)2);
    Trace.println("CacheEntry:  Cannot pre-verify all classes in " + getURL(), TraceLevel.CACHE);
  }

  long getCertificateExpiration()
  {
    return this.certExpirationDate;
  }

  private boolean isOKToUseCachedSecurityValidation()
  {
    if (System.currentTimeMillis() > this.certExpirationDate)
    {
      Trace.println(ResourceManager.getString("cacheEntry.certExpired", this.url, new Date(this.certExpirationDate).toString()), TraceLevel.SECURITY);
      return false;
    }
    if ((Config.getBooleanProperty("deployment.security.validation.crl")) || (Config.getBooleanProperty("deployment.security.validation.ocsp")))
    {
      Trace.println("Certificate revocation enabled. Disable security validation optimizations.", TraceLevel.SECURITY);
      return false;
    }
    return true;
  }

  public synchronized long getValidationTimestamp()
  {
    if (!isOKToUseCachedSecurityValidation())
      resetValidationResults();
    return this.validationTimestampt;
  }

  private synchronized void resetValidationResults()
  {
    this.knownToBeSigned = false;
    this.validationTimestampt = 0L;
  }

  private void updateBlacklistValidation()
  {
    resetValidationResults();
    if (Config.getBooleanProperty("deployment.security.blacklist.check"))
      this.blacklistValidationTime = System.currentTimeMillis();
  }

  private void updateTrustedLibrariesValidation()
  {
    this.trustedLibrariesValidationTime = System.currentTimeMillis();
  }

  public synchronized boolean isKnownToBeSigned()
  {
    if ((this.knownToBeSigned) && (!isOKToUseCachedSecurityValidation()))
    {
      Trace.println(ResourceManager.getString("cacheEntry.resetValidation", this.url), TraceLevel.SECURITY);
      resetValidationResults();
    }
    return this.knownToBeSigned;
  }

  public synchronized byte getClassesVerificationStatus()
  {
    return this.classVerificationStatus;
  }

  private synchronized void updateClassVerificationStatus(byte paramByte)
  {
    this.classVerificationStatus = paramByte;
    try
    {
      updateIndexHeaderOnDisk();
    }
    catch (IOException localIOException)
    {
      Trace.println("Failed to update Class Verification result in the index", TraceLevel.CACHE);
      Trace.ignoredException(localIOException);
    }
  }

  boolean hasSigningInfo()
  {
    return this.section4Length > 15;
  }

  public void updateValidationResults(boolean paramBoolean1, Map paramMap, long paramLong1, long paramLong2, boolean paramBoolean2)
  {
    JarSigningData localJarSigningData = getJarSigningData();
    if (localJarSigningData == null)
      return;
    Map localMap = Config.isJavaVersionAtLeast15() ? localJarSigningData.getSignerMap() : localJarSigningData.getCertificateMap();
    if ((localMap != null) && (hasStrictSingleSigning()))
    {
      Trace.println("updateValidationResultsForApplet update", TraceLevel.BASIC);
      updateValidationResults(paramBoolean1, paramMap, paramLong1, paramLong2);
    }
  }

  private synchronized void updateValidationResults(boolean paramBoolean, Map paramMap, long paramLong1, long paramLong2)
  {
    this.knownToBeSigned = paramBoolean;
    this.validationTimestampt = paramLong1;
    this.certExpirationDate = paramLong2;
    this.checkedJars = paramMap;
    Trace.println("Mark prevalidated: " + this.url + " " + paramBoolean + " tm=" + paramLong1 + " cert=" + paramLong2, TraceLevel.CACHE);
    try
    {
      updateSecurityValidationCache();
      updateIndexHeaderOnDisk();
    }
    catch (IOException localIOException)
    {
      Trace.println("Failed to update list of trusted cached entries in the index", TraceLevel.CACHE);
      Trace.ignoredException(localIOException);
    }
  }

  private void updateSecurityValidationCache()
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          RandomAccessFile localRandomAccessFile = null;
          try
          {
            localRandomAccessFile = CacheEntry.this.openLockIndexFile("rw", false);
            CacheEntry.this.section5Length = 0;
            if ((CacheEntry.this.checkedJars != null) && (!CacheEntry.this.checkedJars.isEmpty()))
            {
              ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(500);
              DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
              Set localSet = CacheEntry.this.checkedJars.entrySet();
              localDataOutputStream.writeInt(localSet.size());
              Iterator localIterator = localSet.iterator();
              while (localIterator.hasNext())
              {
                localObject1 = (Map.Entry)localIterator.next();
                localDataOutputStream.writeUTF((String)((Map.Entry)localObject1).getKey());
                localDataOutputStream.writeLong(((Long)((Map.Entry)localObject1).getValue()).longValue());
              }
              localDataOutputStream.close();
              localByteArrayOutputStream.close();
              Object localObject1 = localByteArrayOutputStream.toByteArray();
              localRandomAccessFile.seek(128 + CacheEntry.this.section2Length + CacheEntry.this.section3Length + CacheEntry.this.section4Length);
              localRandomAccessFile.write((byte[])localObject1);
              CacheEntry.this.section5Length = localObject1.length;
            }
          }
          finally
          {
            if (localRandomAccessFile != null)
            {
              CacheEntry.this.doUpdateHeader(localRandomAccessFile);
              localRandomAccessFile.close();
            }
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      if ((localPrivilegedActionException.getException() instanceof IOException))
        throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  private void readSecurityValidationCache()
    throws IOException
  {
    if (this.section5Length != 0)
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws IOException
          {
            byte[] arrayOfByte = new byte[CacheEntry.this.section5Length];
            RandomAccessFile localRandomAccessFile = CacheEntry.this.openLockIndexFile("rw", false);
            try
            {
              localRandomAccessFile.seek(128 + CacheEntry.this.section2Length + CacheEntry.this.section3Length + CacheEntry.this.section4Length);
              localRandomAccessFile.readFully(arrayOfByte);
            }
            finally
            {
              localRandomAccessFile.close();
            }
            ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
            DataInputStream localDataInputStream = new DataInputStream(localByteArrayInputStream);
            int i = localDataInputStream.readInt();
            HashMap localHashMap = new HashMap();
            while (i > 0)
            {
              String str = localDataInputStream.readUTF();
              Long localLong = new Long(localDataInputStream.readLong());
              localHashMap.put(str, localLong);
              i--;
            }
            CacheEntry.this.checkedJars = localHashMap;
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        if ((localPrivilegedActionException.getException() instanceof IOException))
          throw ((IOException)localPrivilegedActionException.getException());
      }
    else
      this.checkedJars = null;
  }

  public synchronized Map getCachedTrustedEntries()
  {
    if ((this.section5Length == 0) && (this.checkedJars == null))
      return null;
    if (this.checkedJars == null)
      try
      {
        readSecurityValidationCache();
      }
      catch (IOException localIOException)
      {
        Trace.println("Failed to read list of trusted cached entries from index", TraceLevel.CACHE);
        Trace.ignoredException(localIOException);
      }
    return this.checkedJars;
  }

  private void invalidateEntryDueToException(Throwable paramThrowable)
  {
    Trace.println("Invalidating entry url=" + this.url + " file=" + this.indexFile.getAbsolutePath());
    Trace.ignored(paramThrowable);
    invalidateEntry();
  }

  private void invalidateEntry()
  {
    setIncomplete(INCOMPLETE_TRUE);
    try
    {
      updateIndexHeaderOnDisk();
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
    }
  }

  void markIncompleteOnHold()
  {
    setBusy(BUSY_TRUE);
    invalidateEntry();
    setIncomplete(INCOMPLETE_ONHOLD);
  }

  public CacheEntry(File paramFile)
  {
    this(paramFile, false);
  }

  CacheEntry(File paramFile, boolean paramBoolean)
  {
    String str = paramFile.getPath();
    this.filename = str.substring(0, str.length() - 4);
    this.indexFile = paramFile;
    this.indexFileSyncAccess = new SyncFileAccess(this.indexFile);
    this.tempDataFile = new File(this.filename + "-temp");
    AccessController.doPrivileged(new PrivilegedAction()
    {
      private final boolean val$incompleteOK;

      public Object run()
      {
        try
        {
          CacheEntry.this.readIndexFile(this.val$incompleteOK);
        }
        catch (Throwable localThrowable)
        {
          CacheEntry.this.invalidateEntryDueToException(localThrowable);
        }
        return null;
      }
    });
  }

  synchronized void generateShortcutImage()
    throws IOException
  {
    if (getIsShortcutImage() == 0)
    {
      setIsShortcutImage(1);
      updateIndexHeaderOnDisk();
    }
  }

  private static boolean isIssuerOf(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
  {
    Principal localPrincipal1 = paramX509Certificate1.getIssuerDN();
    Principal localPrincipal2 = paramX509Certificate2.getSubjectDN();
    return localPrincipal1.equals(localPrincipal2);
  }

  private RandomAccessFile openLockIndexFile(String paramString, boolean paramBoolean)
    throws IOException
  {
    SyncFileAccess.RandomAccessFileLock localRandomAccessFileLock = null;
    RandomAccessFile localRandomAccessFile1 = null;
    try
    {
      localRandomAccessFileLock = this.indexFileSyncAccess.openLockRandomAccessFile(paramString, 10000, paramBoolean);
      if (localRandomAccessFileLock != null)
        localRandomAccessFile1 = localRandomAccessFileLock.getRandomAccessFile();
      else
        localRandomAccessFile1 = new RandomAccessFile(this.indexFile, paramString);
      RandomAccessFile localRandomAccessFile2 = localRandomAccessFile1;
      return localRandomAccessFile2;
    }
    finally
    {
      if (localRandomAccessFileLock != null)
        localRandomAccessFileLock.release();
    }
  }

  private void readIndexFileOld(DataInputStream paramDataInputStream, RandomAccessFile paramRandomAccessFile)
    throws IOException
  {
    if (!this.indexFile.exists())
      return;
    switch (getCacheVersion())
    {
    case 603:
    case 604:
      readSection1Remaining604(paramDataInputStream);
      readSection2(paramRandomAccessFile);
      return;
    case 605:
      paramRandomAccessFile.close();
      paramRandomAccessFile = null;
      readIndexFile();
      return;
    case 602:
      readIndexFile602(paramRandomAccessFile);
    }
  }

  private void readIndexFile()
  {
    readIndexFile(false);
  }

  private void readIndexFile(boolean paramBoolean)
  {
    RandomAccessFile localRandomAccessFile = null;
    Object localObject1 = null;
    try
    {
      if (this.indexFile.exists())
      {
        localRandomAccessFile = openLockIndexFile("r", false);
        byte[] arrayOfByte = new byte[''];
        int i = localRandomAccessFile.read(arrayOfByte);
        DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte, 0, i));
        setBusy(localDataInputStream.readByte());
        int j = localDataInputStream.readByte();
        if ((paramBoolean) && (j == INCOMPLETE_TRUE))
          j = INCOMPLETE_ONHOLD;
        setIncomplete(j);
        if (isIncomplete())
          return;
        setCacheVersion(localDataInputStream.readInt());
        if (getCacheVersion() != Cache.getCacheVersion())
        {
          Trace.println("In-place upgrading old version " + getCacheVersion() + " file: " + this.indexFile.getAbsolutePath(), TraceLevel.CACHE);
          readIndexFileOld(localDataInputStream, localRandomAccessFile);
          localRandomAccessFile.close();
          localRandomAccessFile = null;
          saveUpgrade();
          return;
        }
        readSection1Remaining(localDataInputStream);
        readSection2(localRandomAccessFile);
        File localFile = new File(getResourceFilename());
        if ((!isRedirectEntry()) && (!localFile.exists()))
        {
          setIncomplete(INCOMPLETE_TRUE);
          return;
        }
        if ((hasSigningInfo()) && (((this.section4Pre15Length == 0) && (enhancedJarAccess)) || (((BlackList.getInstance().hasBeenModifiedSince(this.blacklistValidationTime)) || (TrustedLibraries.hasBeenModifiedSince(this.trustedLibrariesValidationTime))) && ((!Cache.isSystemCacheEntry(this)) || (Environment.isSystemCacheMode())))))
        {
          localRandomAccessFile.close();
          localRandomAccessFile = null;
          saveUpgrade();
          Trace.println("readIndexFile returning success", TraceLevel.CACHE);
        }
      }
    }
    catch (IOException localIOException2)
    {
      localObject1 = localIOException2;
    }
    finally
    {
      try
      {
        if (localRandomAccessFile != null)
          localRandomAccessFile.close();
      }
      catch (IOException localIOException7)
      {
        Trace.ignoredException(localIOException7);
      }
    }
    if (localObject1 != null)
      invalidateEntryDueToException(localObject1);
  }

  void processRedirectData(URL paramURL, CacheEntry paramCacheEntry)
    throws IOException
  {
    setBusy(BUSY_FALSE);
    setIncomplete(INCOMPLETE_FALSE);
    setURL(paramURL.toString());
    setLastModified(paramCacheEntry.getLastModified());
    setExpirationDate(paramCacheEntry.getExpirationDate());
    setVersion(paramCacheEntry.getVersion());
    this.headerFields = new MessageHeader();
    this.headerFields.add(null, String.valueOf(302));
    this.headerFields.add("Location", paramCacheEntry.getURL().toString());
    setHeaders(this.headerFields);
    writeFileToDisk();
  }

  public synchronized void setBusy(int paramInt)
  {
    this.busy = paramInt;
  }

  synchronized int getBusy()
  {
    return this.busy;
  }

  private void setCacheVersion(int paramInt)
  {
    this.cacheVersion = paramInt;
  }

  int getCacheVersion()
  {
    return this.cacheVersion;
  }

  public synchronized void setIncomplete(int paramInt)
  {
    this.incomplete = paramInt;
  }

  public synchronized int getIncomplete()
  {
    return this.incomplete;
  }

  boolean isIncomplete()
  {
    return getIncomplete() == INCOMPLETE_TRUE;
  }

  public synchronized boolean isValidEntry()
  {
    return (this.busy == BUSY_FALSE) && (this.incomplete == INCOMPLETE_FALSE);
  }

  synchronized boolean storageFilesExist()
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        if ((CacheEntry.this.filename != null) && (new File(CacheEntry.this.filename).exists()) && (CacheEntry.this.indexFile != null) && (CacheEntry.this.indexFile.exists()))
          return Boolean.TRUE;
        return Boolean.FALSE;
      }
    });
    return localBoolean.booleanValue();
  }

  public synchronized void setIsShortcutImage(int paramInt)
  {
    this.isShortcutImage = paramInt;
  }

  public synchronized int getIsShortcutImage()
  {
    return this.isShortcutImage;
  }

  public synchronized void setIsProxied(int paramInt)
  {
    this.isProxiedHost = paramInt;
  }

  public synchronized int getIsProxied()
  {
    return this.isProxiedHost;
  }

  synchronized boolean removeBefore(CacheEntry paramCacheEntry)
  {
    if (isIncomplete())
      return true;
    if (paramCacheEntry.isIncomplete())
      return false;
    long l1 = getIndexFile().lastModified();
    long l2 = paramCacheEntry.getIndexFile().lastModified();
    if (l1 < l2)
      return true;
    if (l1 > l2)
      return false;
    long l3 = System.currentTimeMillis();
    long l4 = getExpirationDate();
    long l5 = paramCacheEntry.getExpirationDate();
    if (l4 != l5)
    {
      if (l4 < l3)
        return true;
      if (l5 < l3)
        return false;
    }
    return getContentLength() >= paramCacheEntry.getContentLength();
  }

  synchronized void setContentLength(int paramInt)
  {
    this.contentLength = paramInt;
  }

  public synchronized int getContentLength()
  {
    return this.contentLength;
  }

  synchronized JarSigningData getJarSigningData()
  {
    if (this.signingDataRef == null)
      return null;
    JarSigningData localJarSigningData = (JarSigningData)this.signingDataRef.get();
    if (localJarSigningData == null)
    {
      localJarSigningData = new JarSigningData(this.hasOnlySignedEntries, this.hasSingleCodeSource, this.hasMissingSignedEntries);
      try
      {
        readSigningData(localJarSigningData);
      }
      catch (IOException localIOException1)
      {
        Trace.ignoredException(localIOException1);
        recover();
        try
        {
          readSigningData(localJarSigningData);
        }
        catch (IOException localIOException2)
        {
          invalidateEntryDueToException(localIOException2);
          this.signingDataRef = null;
        }
      }
      if (this.signingDataRef != null)
        this.signingDataRef = new SoftReference(localJarSigningData);
    }
    return localJarSigningData;
  }

  synchronized boolean hasStrictSingleSigning()
  {
    return (this.hasOnlySignedEntries) && (this.hasSingleCodeSource) && (!this.hasMissingSignedEntries);
  }

  private synchronized void readSigningData(JarSigningData paramJarSigningData)
    throws IOException
  {
    if (Config.isJavaVersionAtLeast15())
      readSigners(paramJarSigningData);
    else
      readCertificates(paramJarSigningData);
  }

  public synchronized Certificate[] getCertificates()
  {
    JarSigningData localJarSigningData = getJarSigningData();
    return localJarSigningData != null ? localJarSigningData.getCertificates() : null;
  }

  public synchronized CodeSigner[] getCodeSigners()
  {
    JarSigningData localJarSigningData = getJarSigningData();
    return localJarSigningData != null ? localJarSigningData.getCodeSigners() : null;
  }

  synchronized void setLastModified(long paramLong)
  {
    this.lastModified = paramLong;
  }

  public synchronized long getLastModified()
  {
    return this.lastModified;
  }

  synchronized void setExpirationDate(long paramLong)
  {
    this.expirationDate = paramLong;
  }

  synchronized void updateExpirationInIndexFile(long paramLong)
  {
    if (getExpirationDate() == paramLong)
      return;
    setExpirationDate(paramLong);
    try
    {
      updateIndexHeaderOnDisk();
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
    }
  }

  public synchronized long getExpirationDate()
  {
    return this.expirationDate;
  }

  synchronized boolean isExpired()
  {
    return (this.expirationDate == 0L) || (System.currentTimeMillis() >= this.expirationDate);
  }

  synchronized void setURL(String paramString)
  {
    this.url = paramString;
  }

  public synchronized String getURL()
  {
    return this.url;
  }

  synchronized void setVersion(String paramString)
  {
    if ((paramString == null) || (paramString.equals("")))
      this.version = null;
    else
      this.version = paramString;
  }

  public synchronized String getVersion()
  {
    return this.version;
  }

  private void setNamespaceID(String paramString)
  {
    this.namespaceID = paramString;
  }

  private String getNamespaceID()
  {
    return this.namespaceID;
  }

  synchronized JarFile getRefJar()
  {
    if (this.jarRef != null)
    {
      Object localObject = this.jarRef.get();
      if ((localObject instanceof JarFile))
        return (JarFile)localObject;
    }
    return null;
  }

  public JarFile getJarFile()
  {
    if (isRedirectEntry())
      return null;
    JarFile localJarFile = null;
    synchronized (this)
    {
      if (this.jarRef != null)
      {
        Object localObject1 = this.jarRef.get();
        if ((localObject1 instanceof JarFile))
          return (JarFile)localObject1;
      }
      localJarFile = (JarFile)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            if (Config.isJavaVersionAtLeast15())
              return new CachedJarFile(CacheEntry.this);
            return new CachedJarFile14(CacheEntry.this);
          }
          catch (Exception localException)
          {
            Trace.ignored(localException);
          }
          return null;
        }
      });
      this.jarRef = new WeakReference(localJarFile);
    }
    MemoryCache.addResourceReference(localJarFile, this.url);
    return localJarFile;
  }

  public synchronized String getResourceFilename()
  {
    return this.filename;
  }

  public File getDataFile()
  {
    File localFile = null;
    synchronized (this)
    {
      if ((!isRedirectEntry()) && (this.filename != null) && (!this.url.equals("")))
        localFile = new File(this.filename);
    }
    if (localFile != null)
      MemoryCache.addResourceReference(localFile, this.url);
    return localFile;
  }

  synchronized File getTempDataFile()
  {
    return this.tempDataFile;
  }

  public synchronized File getIndexFile()
  {
    return this.indexFile;
  }

  public synchronized long getSize()
  {
    long l = 0L;
    if (getDataFile() != null)
      l += getDataFile().length();
    if (getIndexFile() != null)
      l += getIndexFile().length();
    return l;
  }

  synchronized void setHeaders(MessageHeader paramMessageHeader)
  {
    this.headerFields = paramMessageHeader;
  }

  MessageHeader cloneHeaders()
  {
    return new MessageHeader(this.headerFields);
  }

  public synchronized Map getHeaders()
  {
    return this.headerFields.getHeaders();
  }

  public synchronized boolean isHttpNoCacheEnabled()
  {
    String str1 = this.headerFields.getValue(this.headerFields.getKey("cache-control"));
    if ((str1 != null) && (str1.equals("no-cache")))
      return true;
    String str2 = this.headerFields.getValue(this.headerFields.getKey("pragma"));
    return (str2 != null) && (str2.equals("no-cache"));
  }

  synchronized boolean processTempDataFile(boolean paramBoolean, DownloadDelegate paramDownloadDelegate, URL paramURL1, URL paramURL2, String paramString)
  {
    boolean bool = false;
    if (paramBoolean)
    {
      localObject = null;
      String str1 = paramURL2.getQuery().toString();
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, "&");
      String str2 = null;
      while (localStringTokenizer.hasMoreTokens())
      {
        str2 = localStringTokenizer.nextToken();
        if (str2.startsWith("current-version-id"))
          localObject = str2.substring("current-version-id".length() + 1);
      }
      CacheEntry localCacheEntry = null;
      localCacheEntry = Cache.getCacheEntry(paramURL1, (String)localObject);
      File localFile1 = null;
      if (localCacheEntry != null)
        localFile1 = new File(localCacheEntry.getResourceFilename());
      File localFile2 = null;
      try
      {
        if (Trace.isEnabled(TraceLevel.NETWORK))
          Trace.println(ResourceManager.getString("cacheEntry.applyJarDiff", paramURL1 == null ? "" : paramURL1.toString(), (String)localObject, paramString), TraceLevel.NETWORK);
        localFile2 = DownloadEngine.applyPatch(localFile1, this.tempDataFile, paramURL1, paramString, paramDownloadDelegate, this.filename);
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          CacheEntry.this.tempDataFile.delete();
          return null;
        }
      });
      if (localFile2 != null)
        bool = true;
    }
    Object localObject = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Boolean.valueOf(CacheEntry.this.tempDataFile.renameTo(new File(CacheEntry.this.filename)));
      }
    });
    if (((Boolean)localObject).booleanValue())
      bool = true;
    return bool;
  }

  private void initFrom(CacheEntry paramCacheEntry)
  {
    Trace.println("Recovering CacheEntry for " + paramCacheEntry.getURL(), TraceLevel.CACHE);
    this.filename = paramCacheEntry.filename;
    this.indexFile = paramCacheEntry.indexFile;
    this.indexFileSyncAccess = new SyncFileAccess(this.indexFile);
    this.tempDataFile = new File(this.filename + "-temp");
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        CacheEntry.this.readIndexFile();
        return null;
      }
    });
  }

  private void recover()
  {
    Trace.println("Trying to recover cache entry for " + this.url, TraceLevel.CACHE);
    try
    {
      URL localURL = new URL(this.url);
      String str1 = getVersion();
      String str2 = getNamespaceID();
      MemoryCache.removeLoadedResource(this.url);
      Cache.removeCacheEntry(this, false);
      CacheEntry localCacheEntry = Cache.getCacheEntry(localURL, str1);
      if (localCacheEntry != null)
        initFrom(localCacheEntry);
      else
        throw new RuntimeException("ERROR: Recovery got null entry");
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
      throw new RuntimeException("ERROR: Failed to recover corrupt cache entry");
    }
  }

  public synchronized Manifest getManifest()
  {
    Manifest localManifest = this.manifestRef != null ? (Manifest)this.manifestRef.get() : null;
    if ((!this.doneReadManifest) || ((this.manifestRef != null) && (localManifest == null)))
      try
      {
        localManifest = readManifest();
      }
      catch (IOException localIOException1)
      {
        Trace.ignoredException(localIOException1);
        recover();
        try
        {
          localManifest = readManifest();
        }
        catch (IOException localIOException2)
        {
          invalidateEntryDueToException(localIOException2);
        }
      }
    return localManifest;
  }

  private synchronized void setCodebaseIP(String paramString)
  {
    this.codebaseIP = paramString;
  }

  public synchronized String getCodebaseIP()
  {
    return this.codebaseIP;
  }

  void writeFileToDisk()
    throws IOException
  {
    writeFileToDisk(1, null);
  }

  public synchronized boolean isJarFile()
  {
    Map localMap = this.headerFields != null ? this.headerFields.getHeaders() : Collections.EMPTY_MAP;
    return HttpUtils.isJarFile(this.url, localMap);
  }

  public synchronized boolean isJNLPFile()
  {
    Map localMap = this.headerFields != null ? this.headerFields.getHeaders() : Collections.EMPTY_MAP;
    return HttpUtils.isJNLPFile(this.url, localMap);
  }

  public synchronized void updateIndexHeaderOnDisk()
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          if ((CacheEntry.this.indexFile != null) && (CacheEntry.this.indexFile.exists()))
          {
            RandomAccessFile localRandomAccessFile = null;
            try
            {
              localRandomAccessFile = CacheEntry.this.openLockIndexFile("rw", false);
              CacheEntry.this.doUpdateHeader(localRandomAccessFile);
            }
            finally
            {
              if (localRandomAccessFile != null)
                localRandomAccessFile.close();
            }
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      if ((localPrivilegedActionException.getException() instanceof IOException))
        throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  private void doUpdateHeader(RandomAccessFile paramRandomAccessFile)
    throws IOException
  {
    if (paramRandomAccessFile != null)
    {
      paramRandomAccessFile.seek(0L);
      paramRandomAccessFile.write(prepareHeader());
    }
  }

  private byte[] prepareHeader()
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(128);
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    localDataOutputStream.writeByte(this.busy);
    localDataOutputStream.writeByte(this.incomplete);
    localDataOutputStream.writeInt(this.cacheVersion);
    localDataOutputStream.writeByte(this.isShortcutImage);
    localDataOutputStream.writeInt(this.contentLength);
    localDataOutputStream.writeLong(this.lastModified);
    localDataOutputStream.writeLong(this.expirationDate);
    localDataOutputStream.writeLong(this.validationTimestampt);
    localDataOutputStream.writeByte(this.knownToBeSigned ? 1 : 0);
    localDataOutputStream.writeInt(this.section2Length);
    localDataOutputStream.writeInt(this.section3Length);
    localDataOutputStream.writeInt(this.section4Length);
    localDataOutputStream.writeInt(this.section5Length);
    localDataOutputStream.writeLong(this.blacklistValidationTime);
    localDataOutputStream.writeLong(this.certExpirationDate);
    localDataOutputStream.writeByte(this.classVerificationStatus);
    localDataOutputStream.writeInt(this.reducedManifestLength);
    localDataOutputStream.writeInt(this.section4Pre15Length);
    localDataOutputStream.writeByte(this.hasOnlySignedEntries ? 1 : 0);
    localDataOutputStream.writeByte(this.hasSingleCodeSource ? 1 : 0);
    localDataOutputStream.writeInt(this.section4CertsLength);
    localDataOutputStream.writeInt(this.section4SignersLength);
    localDataOutputStream.writeByte(this.hasMissingSignedEntries ? 1 : 0);
    localDataOutputStream.writeLong(this.trustedLibrariesValidationTime);
    localDataOutputStream.writeInt(this.reducedManifest2Length);
    localDataOutputStream.writeByte(this.isProxiedHost);
    localDataOutputStream.flush();
    if (localByteArrayOutputStream.size() < 128)
    {
      byte[] arrayOfByte = new byte[128 - localByteArrayOutputStream.size()];
      localDataOutputStream.write(arrayOfByte);
    }
    else
    {
      Trace.println("Incorrect header size: " + localByteArrayOutputStream.size(), TraceLevel.CACHE);
    }
    localDataOutputStream.close();
    return localByteArrayOutputStream.toByteArray();
  }

  synchronized void writeFileToDisk(int paramInt, DownloadDelegate paramDownloadDelegate)
    throws IOException
  {
    writeFileToDisk(paramInt, paramDownloadDelegate, true);
  }

  synchronized void writeFileToDisk(int paramInt, DownloadDelegate paramDownloadDelegate, boolean paramBoolean)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final int val$contentType;
        private final DownloadDelegate val$dd;
        private final boolean val$newEntry;

        public Object run()
          throws IOException
        {
          Object localObject1 = null;
          RandomAccessFile localRandomAccessFile = null;
          CacheEntry.this.section2Length = 0;
          CacheEntry.this.section3Length = 0;
          CacheEntry.this.section4Length = 0;
          CacheEntry.this.section4Pre15Length = 0;
          CacheEntry.this.section4CertsLength = 0;
          CacheEntry.this.section4SignersLength = 0;
          CacheEntry.this.section5Length = 0;
          CacheEntry.this.reducedManifestLength = 0;
          CacheEntry.this.reducedManifest2Length = 0;
          try
          {
            localRandomAccessFile = CacheEntry.this.openLockIndexFile("rw", false);
            byte[] arrayOfByte = CacheEntry.this.prepareHeader();
            localRandomAccessFile.write(arrayOfByte);
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1000);
            DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
            localDataOutputStream.writeUTF(CacheEntry.this.getVersion() != null ? CacheEntry.this.getVersion() : "");
            localDataOutputStream.writeUTF(CacheEntry.this.getURL());
            localDataOutputStream.writeUTF(CacheEntry.this.getNamespaceID());
            InetAddress localInetAddress = null;
            String str1 = "";
            if ((CacheEntry.this.url != null) && (!CacheEntry.this.url.equals("")))
            {
              URL localURL = new URL(CacheEntry.this.url);
              String str2 = localURL.getHost();
              localInetAddress = Cache.getHostIP(str2);
              if (localInetAddress != null)
                str1 = localInetAddress.getHostAddress();
            }
            localDataOutputStream.writeUTF(str1);
            CacheEntry.this.writeHeaders(localDataOutputStream);
            localDataOutputStream.close();
            localByteArrayOutputStream.close();
            CacheEntry.this.section2Length = localByteArrayOutputStream.size();
            localRandomAccessFile.write(localByteArrayOutputStream.toByteArray());
            if (CacheEntry.this.incomplete == 0)
            {
              if ((CacheEntry.this.isJarFile()) && (!CacheEntry.this.hasCompressEncoding()) && (!CacheEntry.this.isRedirectEntry()))
                CacheEntry.this.processJar(localRandomAccessFile, this.val$contentType, this.val$dd);
              if (this.val$newEntry)
                UpdateTracker.checkDone(CacheEntry.this.url);
              Cache.addToCleanupThreadLoadedResourceList(CacheEntry.this.url);
              CacheEntry.this.setBusy(CacheEntry.BUSY_FALSE);
              CacheEntry.this.setIncomplete(CacheEntry.INCOMPLETE_FALSE);
              CacheEntry.this.updateBlacklistValidation();
              CacheEntry.this.updateTrustedLibrariesValidation();
              CacheEntry.this.doUpdateHeader(localRandomAccessFile);
              CacheEntry.this.doneReadManifest = true;
            }
          }
          catch (Exception localException)
          {
            Trace.ignoredException(localException);
            if (localRandomAccessFile != null)
            {
              localRandomAccessFile.close();
              localRandomAccessFile = null;
            }
            if (localObject1 != null)
            {
              localObject1.close();
              localObject1 = null;
            }
            Cache.removeCacheEntry(CacheEntry.this);
            if ((localException instanceof JARSigningException))
              throw ((JARSigningException)localException);
            if ((localException instanceof ZipException))
              throw new JARSigningException(new URL(CacheEntry.this.url), CacheEntry.this.version, 2, localException);
            throw new IOException(localException.getMessage());
          }
          finally
          {
            if (localRandomAccessFile != null)
              localRandomAccessFile.close();
            if (localObject1 != null)
              localObject1.close();
            Cache.cleanup();
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      if ((localPrivilegedActionException.getException() instanceof IOException))
        throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  boolean isRedirectEntry()
  {
    return getRedirectFinalURL() != null;
  }

  public URL getRedirectFinalURL()
  {
    Map localMap = getHeaders();
    List localList = localMap == null ? null : (List)localMap.get("Location");
    String str = (localList == null) || (localList.size() < 1) ? null : (String)localList.get(0);
    if (str != null)
      try
      {
        return new URL(str);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        Trace.ignored(localMalformedURLException);
      }
    return null;
  }

  private void updateManifestRefs(Manifest paramManifest)
  {
    if (paramManifest != null)
      this.manifestRef = new SoftReference(paramManifest);
    else
      this.manifestRef = null;
  }

  private byte[] readBlock(int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      return (byte[])AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final int val$offset;
        private final int val$length;

        public Object run()
          throws IOException
        {
          RandomAccessFile localRandomAccessFile = CacheEntry.this.openLockIndexFile("r", false);
          try
          {
            localRandomAccessFile.seek(this.val$offset);
            Object localObject1 = null;
            byte[] arrayOfByte1 = new byte[this.val$length];
            localRandomAccessFile.readFully(arrayOfByte1);
            byte[] arrayOfByte2 = arrayOfByte1;
            return arrayOfByte2;
          }
          finally
          {
            if (localRandomAccessFile != null)
              localRandomAccessFile.close();
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      if ((localPrivilegedActionException.getException() instanceof IOException))
        throw ((IOException)localPrivilegedActionException.getException());
    }
    return null;
  }

  synchronized byte[] getFullManifestBytes()
    throws IOException
  {
    int i;
    int j;
    if (this.reducedManifest2Length > 0)
    {
      i = 128 + this.section2Length;
      j = this.section3Length - this.reducedManifest2Length;
      return readBlock(i, j);
    }
    if (this.reducedManifestLength > 0)
    {
      i = 128 + this.section2Length + this.reducedManifestLength;
      j = this.section3Length - this.reducedManifestLength;
      return readBlock(i, j);
    }
    return null;
  }

  synchronized Manifest readManifest()
    throws IOException
  {
    CachedManifest localCachedManifest = null;
    if (this.section3Length != 0)
    {
      int j = 128 + this.section2Length;
      int i;
      if (this.reducedManifest2Length > 0)
      {
        j += this.section3Length - this.reducedManifest2Length;
        i = this.reducedManifest2Length;
      }
      else if (this.reducedManifestLength > 0)
      {
        i = this.reducedManifestLength;
      }
      else
      {
        i = this.section3Length;
      }
      byte[] arrayOfByte = readBlock(j, i);
      Trace.println(" Read manifest for " + this.url + ": read=" + i + " full=" + this.section3Length, TraceLevel.CACHE);
      localCachedManifest = new CachedManifest(getURL(), arrayOfByte, (this.reducedManifest2Length > 0) || (this.reducedManifestLength > 0));
      updateManifestRefs(localCachedManifest);
    }
    this.doneReadManifest = true;
    return localCachedManifest;
  }

  private void readCertificates(JarSigningData paramJarSigningData)
    throws IOException
  {
    int i = this.section4Pre15Length != 0 ? this.section4Pre15Length : this.section4Length;
    int j = hasStrictSingleSigning() ? this.section4CertsLength : i;
    Trace.println("Reading certificates from " + j + " " + this.url + " | " + this.indexFile.getAbsolutePath(), TraceLevel.CACHE);
    if (this.section4Length != 0)
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          private final int val$certsLength;
          private final JarSigningData val$jsd;

          public Object run()
            throws IOException
          {
            RandomAccessFile localRandomAccessFile = CacheEntry.this.openLockIndexFile("r", false);
            try
            {
              localRandomAccessFile.seek(128 + CacheEntry.this.section2Length + CacheEntry.this.section3Length);
              byte[] arrayOfByte = new byte[this.val$certsLength];
              localRandomAccessFile.readFully(arrayOfByte);
              ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
              BufferedInputStream localBufferedInputStream = new BufferedInputStream(localByteArrayInputStream);
              IndexFileObjectInputStream localIndexFileObjectInputStream = new IndexFileObjectInputStream(localBufferedInputStream);
              BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localIndexFileObjectInputStream));
              CacheEntry.this.readCertificates(localIndexFileObjectInputStream, localBufferedReader, this.val$jsd);
              if (Trace.isEnabled(TraceLevel.CACHE))
                Trace.println("Done readCertificates(" + CacheEntry.this.getURL() + "): " + CacheEntry.asString(this.val$jsd.getCertificates()), TraceLevel.CACHE);
            }
            finally
            {
              if (localRandomAccessFile != null)
                localRandomAccessFile.close();
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        if ((localPrivilegedActionException.getException() instanceof IOException))
          throw ((IOException)localPrivilegedActionException.getException());
      }
  }

  private static String asString(Certificate[] paramArrayOfCertificate)
  {
    if (paramArrayOfCertificate != null)
      return Arrays.asList(paramArrayOfCertificate).toString();
    return "null";
  }

  private void readSigners(JarSigningData paramJarSigningData)
    throws IOException
  {
    boolean bool = (this.section4Pre15Length != 0) && (this.section4SignersLength < 5);
    int i = (this.section4Pre15Length != 0) && ((!bool) || (hasStrictSingleSigning())) ? this.section4CertsLength : this.section4Length - (this.section4Length - this.section4Pre15Length);
    int j = (hasStrictSingleSigning()) || (bool) ? this.section4SignersLength : this.section4Length - this.section4Pre15Length;
    Trace.println("Reading Signers from " + j + " " + this.url + " | " + this.indexFile.getAbsolutePath(), TraceLevel.CACHE);
    if (this.section4Length != 0)
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          private final int val$certsLength;
          private final int val$signersLength;
          private final boolean val$processCertificates;
          private final JarSigningData val$jsd;

          public Object run()
            throws IOException
          {
            RandomAccessFile localRandomAccessFile = CacheEntry.this.openLockIndexFile("r", false);
            try
            {
              byte[] arrayOfByte = new byte[this.val$certsLength + this.val$signersLength];
              if (this.val$certsLength != 0)
              {
                localRandomAccessFile.seek(128 + CacheEntry.this.section2Length + CacheEntry.this.section3Length);
                localRandomAccessFile.readFully(arrayOfByte, 0, this.val$certsLength);
              }
              localRandomAccessFile.seek(128 + CacheEntry.this.section2Length + CacheEntry.this.section3Length + CacheEntry.this.section4Pre15Length);
              localRandomAccessFile.readFully(arrayOfByte, this.val$certsLength, this.val$signersLength);
              ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
              BufferedInputStream localBufferedInputStream = new BufferedInputStream(localByteArrayInputStream);
              IndexFileObjectInputStream localIndexFileObjectInputStream = new IndexFileObjectInputStream(localBufferedInputStream);
              BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localIndexFileObjectInputStream));
              if ((this.val$signersLength == CacheEntry.this.section4Length) || (this.val$processCertificates))
              {
                CacheEntry.this.readCertificates(localIndexFileObjectInputStream, localBufferedReader, this.val$jsd);
                if (Trace.isEnabled(TraceLevel.CACHE))
                  Trace.println("Done readCertificates(" + CacheEntry.this.getURL() + "): " + CacheEntry.asString(this.val$jsd.getCertificates()), TraceLevel.CACHE);
              }
              else
              {
                int i = localIndexFileObjectInputStream.readInt();
                try
                {
                  for (int j = 0; j < i; j++)
                    localIndexFileObjectInputStream.readObject();
                }
                catch (ClassNotFoundException localClassNotFoundException)
                {
                  throw new IOException("Error reading signer certificates");
                }
              }
              CacheEntry.this.readSigners(localIndexFileObjectInputStream, localBufferedReader, this.val$jsd);
              if (Trace.isEnabled(TraceLevel.CACHE))
                Trace.println("Done readSigners(" + CacheEntry.this.getURL() + ")", TraceLevel.CACHE);
            }
            finally
            {
              if (localRandomAccessFile != null)
                localRandomAccessFile.close();
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        if ((localPrivilegedActionException.getException() instanceof IOException))
          throw ((IOException)localPrivilegedActionException.getException());
      }
  }

  private void readCertificates(ObjectInputStream paramObjectInputStream, BufferedReader paramBufferedReader, JarSigningData paramJarSigningData)
    throws IOException
  {
    int i = paramObjectInputStream.readInt();
    if (i > 0)
    {
      HashMap localHashMap1 = new HashMap();
      HashMap localHashMap2 = new HashMap();
      Certificate[] arrayOfCertificate1 = new Certificate[i];
      try
      {
        for (int j = 0; j < i; j++)
          arrayOfCertificate1[j] = ((Certificate)paramObjectInputStream.readObject());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new IOException("Error reading signer certificates: " + localClassNotFoundException);
      }
      int[] arrayOfInt = null;
      if (hasStrictSingleSigning())
      {
        arrayOfInt = new int[arrayOfCertificate1.length];
        for (int k = 0; k < arrayOfCertificate1.length; k++)
          arrayOfInt[k] = k;
        localObject1 = createCodeSource(arrayOfCertificate1);
        localHashMap1.put(null, arrayOfInt);
        localHashMap2.put(arrayOfInt, localObject1);
        paramJarSigningData.updateCertInfo(arrayOfCertificate1, localHashMap1, localHashMap2);
        return;
      }
      Object localObject1 = paramBufferedReader.readLine();
      String str = null;
      HashMap localHashMap3 = new HashMap();
      Object localObject2 = null;
      while ((localObject1 != null) && (!((String)localObject1).equals("")))
      {
        Object localObject3 = localObject1;
        if ((str != null) && (((String)localObject3).startsWith("/")))
        {
          localObject3 = str + (String)localObject3;
        }
        else if (((String)localObject3).startsWith("\t"))
        {
          localObject3 = ((String)localObject3).substring(1);
          str = null;
        }
        else
        {
          int m = ((String)localObject3).lastIndexOf("/");
          if (m > 0)
            str = ((String)localObject3).substring(0, m);
          else
            str = null;
        }
        localObject1 = paramBufferedReader.readLine();
        if (localObject2 != null)
        {
          localHashMap1.put(localObject3, localObject2);
        }
        else
        {
          arrayOfInt = (int[])localHashMap3.get(localObject1);
          if (arrayOfInt == null)
          {
            StringTokenizer localStringTokenizer = new StringTokenizer((String)localObject1, " ", false);
            int n = Integer.parseInt(localStringTokenizer.nextToken());
            arrayOfInt = new int[n];
            for (int i1 = 0; i1 < n; i1++)
              arrayOfInt[i1] = Integer.parseInt(localStringTokenizer.nextToken());
            localHashMap3.put(localObject1, arrayOfInt);
            Certificate[] arrayOfCertificate2 = new Certificate[arrayOfInt.length];
            for (int i2 = 0; i2 < arrayOfInt.length; i2++)
              arrayOfCertificate2[i2] = arrayOfCertificate1[arrayOfInt[i2]];
            CodeSource localCodeSource = createCodeSource(arrayOfCertificate2);
            localHashMap2.put(arrayOfInt, localCodeSource);
          }
          localHashMap1.put(localObject3, arrayOfInt);
          if (this.hasSingleCodeSource)
            localObject2 = arrayOfInt;
        }
        localObject1 = paramBufferedReader.readLine();
      }
      paramJarSigningData.updateCertInfo(arrayOfCertificate1, localHashMap1, localHashMap2);
    }
    else
    {
      Trace.println(ResourceManager.getString("cacheEntry.unsignedJar", this.url), TraceLevel.NETWORK);
    }
  }

  private void readSigners(ObjectInputStream paramObjectInputStream, BufferedReader paramBufferedReader, JarSigningData paramJarSigningData)
    throws IOException
  {
    String str = paramBufferedReader.readLine();
    int i = 0;
    if (!Config.isJavaVersionAtLeast15())
    {
      Trace.println("readSigners called pre-1.5 ", TraceLevel.CACHE);
      return;
    }
    if (str != null)
      i = Integer.parseInt(str);
    if ((i < 1) && ((paramJarSigningData.getCertificateMap() == null) || (paramJarSigningData.getCodeSourceCertCache() == null)))
    {
      Trace.println(ResourceManager.getString("cacheEntry.unsignedJar", this.url), TraceLevel.NETWORK);
      return;
    }
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    CodeSigner[] arrayOfCodeSigner1 = null;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    int i2;
    if (i == 0)
    {
      ArrayList localArrayList = new ArrayList();
      HashMap localHashMap3 = new HashMap();
      localObject2 = paramJarSigningData.getCodeSourceCertCache().entrySet().iterator();
      Object localObject5;
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        localObject4 = (int[])((Map.Entry)localObject3).getKey();
        localObject5 = (CodeSource)((Map.Entry)localObject3).getValue();
        CodeSigner[] arrayOfCodeSigner2 = ((CodeSource)localObject5).getCodeSigners();
        arrayOfCodeSigner2 = convertCertArrayToSignerArray(((CodeSource)localObject5).getCertificates());
        localObject5 = new CodeSource(((CodeSource)localObject5).getLocation(), arrayOfCodeSigner2);
        if ((arrayOfCodeSigner2 != null) && (arrayOfCodeSigner2.length > 0))
        {
          int[] arrayOfInt2 = new int[arrayOfCodeSigner2.length];
          for (i2 = 0; i2 < arrayOfCodeSigner2.length; i2++)
          {
            int i3 = localArrayList.indexOf(arrayOfCodeSigner2[i2]);
            if (i3 == -1)
            {
              i3 = localArrayList.size();
              localArrayList.add(arrayOfCodeSigner2[i2]);
            }
            arrayOfInt2[i2] = i3;
          }
          localHashMap3.put(localObject4, arrayOfInt2);
          localHashMap2.put(arrayOfInt2, localObject5);
        }
      }
      localObject2 = paramJarSigningData.getCertificateMap().entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        localObject4 = (String)((Map.Entry)localObject3).getKey();
        localObject5 = (int[])((Map.Entry)localObject3).getValue();
        localHashMap1.put(localObject4, localHashMap3.get(localObject5));
      }
      arrayOfCodeSigner1 = (CodeSigner[])localArrayList.toArray(new CodeSigner[localArrayList.size()]);
    }
    else if (i > 0)
    {
      arrayOfCodeSigner1 = new CodeSigner[i];
      try
      {
        for (int j = 0; j < i; j++)
          arrayOfCodeSigner1[j] = newCodeSigner((CodeSigner)paramObjectInputStream.readObject());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new IOException("Error reading code signer");
      }
      int[] arrayOfInt1 = null;
      Object localObject1;
      if (hasStrictSingleSigning())
      {
        arrayOfInt1 = new int[arrayOfCodeSigner1.length];
        for (int k = 0; k < arrayOfCodeSigner1.length; k++)
          arrayOfInt1[k] = k;
        localObject1 = createCodeSource(arrayOfCodeSigner1);
        localHashMap1.put(null, arrayOfInt1);
        localHashMap2.put(arrayOfInt1, localObject1);
      }
      else
      {
        localObject1 = paramBufferedReader.readLine();
        localObject2 = null;
        localObject3 = new HashMap();
        while ((localObject1 != null) && (!((String)localObject1).equals("")))
        {
          localObject4 = localObject1;
          if ((localObject2 != null) && (((String)localObject4).startsWith("/")))
          {
            localObject4 = (String)localObject2 + (String)localObject4;
          }
          else if (((String)localObject4).startsWith("\t"))
          {
            localObject4 = ((String)localObject4).substring(1);
            localObject2 = null;
          }
          else
          {
            int m = ((String)localObject4).lastIndexOf("/");
            if (m > 0)
              localObject2 = ((String)localObject4).substring(0, m);
            else
              localObject2 = null;
          }
          localObject1 = paramBufferedReader.readLine();
          arrayOfInt1 = (int[])((Map)localObject3).get(localObject1);
          if (arrayOfInt1 == null)
          {
            StringTokenizer localStringTokenizer = new StringTokenizer((String)localObject1, " ", false);
            int n = Integer.parseInt(localStringTokenizer.nextToken());
            arrayOfInt1 = new int[n];
            for (int i1 = 0; i1 < n; i1++)
              arrayOfInt1[i1] = Integer.parseInt(localStringTokenizer.nextToken());
            ((Map)localObject3).put(localObject1, arrayOfInt1);
            CodeSigner[] arrayOfCodeSigner3 = new CodeSigner[arrayOfInt1.length];
            for (i2 = 0; i2 < arrayOfInt1.length; i2++)
              arrayOfCodeSigner3[i2] = arrayOfCodeSigner1[arrayOfInt1[i2]];
            CodeSource localCodeSource = createCodeSource(arrayOfCodeSigner3);
            localHashMap2.put(arrayOfInt1, localCodeSource);
          }
          localHashMap1.put(localObject4, arrayOfInt1);
          localObject1 = paramBufferedReader.readLine();
        }
      }
    }
    paramJarSigningData.updateSignerInfo(arrayOfCodeSigner1, localHashMap1, localHashMap2);
  }

  private CodeSigner[] convertCertArrayToSignerArray(Certificate[] paramArrayOfCertificate)
    throws IOException
  {
    try
    {
      CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      int j = 0;
      int k = 0;
      while (k < paramArrayOfCertificate.length)
      {
        localObject1 = new ArrayList();
        m = j;
        for (m = j; m < paramArrayOfCertificate.length; m++)
        {
          localObject2 = null;
          Object localObject3 = null;
          if ((paramArrayOfCertificate[m] instanceof X509Certificate))
            localObject2 = (X509Certificate)paramArrayOfCertificate[m];
          if ((m + 1 < paramArrayOfCertificate.length) && ((paramArrayOfCertificate[(m + 1)] instanceof X509Certificate)))
            localObject3 = (X509Certificate)paramArrayOfCertificate[(m + 1)];
          else
            localObject3 = localObject2;
          ((ArrayList)localObject1).add(localObject2);
          if (!isIssuerOf((X509Certificate)localObject2, (X509Certificate)localObject3))
            break;
        }
        k = m < paramArrayOfCertificate.length ? m + 1 : m;
        Object localObject2 = localCertificateFactory.generateCertPath((List)localObject1);
        ((ArrayList)localObject1).clear();
        localArrayList.add(localObject2);
        j = k;
        i++;
      }
      Object localObject1 = new CodeSigner[i];
      for (int m = 0; m < i; m++)
        localObject1[m] = new CodeSigner((CertPath)(CertPath)localArrayList.get(m), (Timestamp)null);
      return localObject1;
    }
    catch (CertificateException localCertificateException)
    {
    }
    throw new IOException("Error process signer certificates");
  }

  private CodeSigner newCodeSigner(CodeSigner paramCodeSigner)
  {
    CertPath localCertPath = paramCodeSigner.getSignerCertPath();
    Timestamp localTimestamp = paramCodeSigner.getTimestamp();
    CodeSigner localCodeSigner = new CodeSigner(localCertPath, newTimestamp(localTimestamp));
    return localCodeSigner;
  }

  private Timestamp newTimestamp(Timestamp paramTimestamp)
  {
    if (paramTimestamp == null)
      return null;
    Date localDate = paramTimestamp.getTimestamp();
    CertPath localCertPath = paramTimestamp.getSignerCertPath();
    return new Timestamp(localDate, localCertPath);
  }

  public String getNativeLibPath()
  {
    return getResourceFilename() + "-n";
  }

  private void processJar(RandomAccessFile paramRandomAccessFile, int paramInt, DownloadDelegate paramDownloadDelegate)
    throws IOException, ParsingException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    URL localURL = new URL(this.url);
    File localFile1 = new File(this.filename);
    File localFile2 = null;
    if (DownloadEngine.isNativeContentType(paramInt))
      localFile2 = new File(getNativeLibPath()).getCanonicalFile();
    JarVerifier localJarVerifier = JarVerifier.create(localURL, this.version, localFile1, localFile2);
    localJarVerifier.validate(paramDownloadDelegate);
    this.hasOnlySignedEntries = localJarVerifier.hasOnlySignedEntries();
    this.hasSingleCodeSource = localJarVerifier.hasSingleCodeSource();
    this.hasMissingSignedEntries = localJarVerifier.hasMissingSignedEntries();
    CachedManifest localCachedManifest = null;
    Manifest localManifest = localJarVerifier.getManifest();
    if (localManifest != null)
    {
      localCachedManifest = new CachedManifest(localManifest);
      this.section3Length = localCachedManifest.writeFull(localByteArrayOutputStream);
      this.reducedManifest2Length = localCachedManifest.writeReduced(localByteArrayOutputStream);
      this.section3Length += this.reducedManifest2Length;
    }
    else
    {
      this.section3Length = 0;
      this.reducedManifest2Length = 0;
    }
    ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
    BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(localObjectOutputStream));
    writeCertificates(localJarVerifier, localObjectOutputStream, localBufferedWriter, localByteArrayOutputStream);
    writeSigners(localJarVerifier, localObjectOutputStream, localBufferedWriter, localByteArrayOutputStream);
    this.signingDataRef = new SoftReference(JarSigningData.create(localJarVerifier));
    paramRandomAccessFile.write(localByteArrayOutputStream.toByteArray());
    if (localCachedManifest != null)
    {
      localCachedManifest.postprocess();
      updateManifestRefs(localCachedManifest);
    }
  }

  private void writeCertificates(JarVerifier paramJarVerifier, ObjectOutputStream paramObjectOutputStream, BufferedWriter paramBufferedWriter, ByteArrayOutputStream paramByteArrayOutputStream)
    throws IOException
  {
    if (!paramJarVerifier.getSignerCerts().isEmpty())
    {
      paramObjectOutputStream.writeInt(paramJarVerifier.getSignerCerts().size());
      Iterator localIterator1 = paramJarVerifier.getSignerCerts().iterator();
      while (localIterator1.hasNext())
        paramObjectOutputStream.writeObject(localIterator1.next());
      paramObjectOutputStream.flush();
      if (!enhancedJarAccess)
        this.section4CertsLength = 0;
      else
        this.section4CertsLength = (paramByteArrayOutputStream.size() - this.section3Length);
      Map localMap = paramJarVerifier.getSignerMapCert();
      Iterator localIterator2 = localMap.keySet().iterator();
      Object localObject = null;
      while (localIterator2.hasNext())
      {
        String str1 = (String)localIterator2.next();
        if ((str1 != null) && (str1.length() != 0))
        {
          int[] arrayOfInt = (int[])localMap.get(str1);
          if (str1.startsWith("/"))
          {
            str1 = "\t" + str1;
            localObject = null;
          }
          else
          {
            int i = str1.lastIndexOf("/");
            if (i > 0)
            {
              String str3 = str1.substring(0, i);
              if (str3.equals(localObject))
                str1 = str1.substring(str3.length());
              else
                localObject = str3;
            }
            else
            {
              localObject = null;
            }
          }
          paramBufferedWriter.write(str1);
          paramBufferedWriter.newLine();
          String str2 = String.valueOf(arrayOfInt.length);
          for (int j = 0; j < arrayOfInt.length; j++)
            str2 = str2 + " " + arrayOfInt[j];
          paramBufferedWriter.write(str2, 0, str2.length());
          paramBufferedWriter.newLine();
        }
      }
      paramBufferedWriter.newLine();
      paramBufferedWriter.flush();
      if (hasStrictSingleSigning())
      {
        localMap.clear();
        localMap.put(null, paramJarVerifier.getSingleSignerIndicesCert());
      }
    }
    else
    {
      paramObjectOutputStream.writeInt(0);
      paramObjectOutputStream.flush();
      if (!enhancedJarAccess)
        this.section4CertsLength = 0;
      else
        this.section4CertsLength = (paramByteArrayOutputStream.size() - this.section3Length);
    }
    paramObjectOutputStream.flush();
    if (!enhancedJarAccess)
      this.section4Pre15Length = 0;
    else
      this.section4Pre15Length = (paramByteArrayOutputStream.size() - this.section3Length);
  }

  private void writeSigners(JarVerifier paramJarVerifier, ObjectOutputStream paramObjectOutputStream, BufferedWriter paramBufferedWriter, ByteArrayOutputStream paramByteArrayOutputStream)
    throws IOException
  {
    List localList = paramJarVerifier.getSignersCS();
    if (!localList.isEmpty())
    {
      Integer localInteger = new Integer(localList.size());
      paramBufferedWriter.write(localInteger.toString());
      paramBufferedWriter.newLine();
      paramBufferedWriter.flush();
      Iterator localIterator1 = localList.iterator();
      while (localIterator1.hasNext())
        paramObjectOutputStream.writeObject(localIterator1.next());
      paramObjectOutputStream.flush();
      if (!enhancedJarAccess)
        this.section4SignersLength = 0;
      else
        this.section4SignersLength = (paramByteArrayOutputStream.size() - (this.section3Length + this.section4Pre15Length));
      Map localMap = paramJarVerifier.getSignerMap();
      Iterator localIterator2 = localMap.keySet().iterator();
      Object localObject = null;
      while (localIterator2.hasNext())
      {
        String str1 = (String)localIterator2.next();
        if ((str1 != null) && (str1.length() != 0))
        {
          int[] arrayOfInt = (int[])localMap.get(str1);
          if (str1.startsWith("/"))
          {
            str1 = "\t" + str1;
            localObject = null;
          }
          else
          {
            int i = str1.lastIndexOf("/");
            if (i > 0)
            {
              String str3 = str1.substring(0, i);
              if (str3.equals(localObject))
                str1 = str1.substring(i);
              else
                localObject = str3;
            }
            else
            {
              localObject = null;
            }
          }
          paramBufferedWriter.write(str1);
          paramBufferedWriter.newLine();
          String str2 = String.valueOf(arrayOfInt.length);
          for (int j = 0; j < arrayOfInt.length; j++)
            str2 = str2 + " " + arrayOfInt[j];
          paramBufferedWriter.write(str2, 0, str2.length());
          paramBufferedWriter.newLine();
        }
      }
      if (hasStrictSingleSigning())
      {
        localMap.clear();
        localMap.put(null, paramJarVerifier.getSingleSignerIndicesCS());
      }
    }
    else
    {
      paramBufferedWriter.write("0");
      paramBufferedWriter.newLine();
      paramBufferedWriter.flush();
      paramObjectOutputStream.flush();
      if (!enhancedJarAccess)
        this.section4SignersLength = 0;
      else
        this.section4SignersLength = (paramByteArrayOutputStream.size() - (this.section3Length + this.section4Pre15Length));
    }
    paramBufferedWriter.flush();
    paramObjectOutputStream.flush();
    this.section4Length = (paramByteArrayOutputStream.size() - this.section3Length);
  }

  private void readIndexFile602(RandomAccessFile paramRandomAccessFile)
    throws IOException
  {
    paramRandomAccessFile.seek(0L);
    paramRandomAccessFile.readByte();
    paramRandomAccessFile.readByte();
    paramRandomAccessFile.readInt();
    paramRandomAccessFile.read();
    paramRandomAccessFile.read();
    setIsShortcutImage(paramRandomAccessFile.read());
    setContentLength(paramRandomAccessFile.readInt());
    setLastModified(paramRandomAccessFile.readLong());
    setExpirationDate(paramRandomAccessFile.readLong());
    setVersion(paramRandomAccessFile.readUTF());
    setURL(paramRandomAccessFile.readUTF());
    setNamespaceID(paramRandomAccessFile.readUTF());
    File localFile = new File(getResourceFilename());
    if ((!isRedirectEntry()) && (!localFile.exists()))
      invalidateEntry();
    if (!isIncomplete())
      readHeaders602(paramRandomAccessFile);
  }

  private void readHeaders602(RandomAccessFile paramRandomAccessFile)
    throws IOException
  {
    try
    {
      for (int i = paramRandomAccessFile.readInt(); i > 0; i--)
      {
        String str = paramRandomAccessFile.readUTF();
        if (str.equals("deploy_resource_codebase_ip"))
        {
          setCodebaseIP(paramRandomAccessFile.readUTF());
        }
        else
        {
          if (str.equals("<null>"))
            str = null;
          this.headerFields.add(str, paramRandomAccessFile.readUTF());
        }
      }
    }
    finally
    {
    }
  }

  private void readHeaders(DataInputStream paramDataInputStream)
    throws IOException
  {
    try
    {
      for (int i = paramDataInputStream.readInt(); i > 0; i--)
      {
        String str = paramDataInputStream.readUTF();
        if (str.equals("<null>"))
          str = null;
        this.headerFields.add(str, paramDataInputStream.readUTF());
      }
    }
    finally
    {
    }
  }

  private void writeHeaders(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    try
    {
      if (this.headerFields == null)
      {
        paramDataOutputStream.writeInt(0);
        return;
      }
      Map localMap = this.headerFields.getHeaders();
      if (!localMap.isEmpty())
      {
        paramDataOutputStream.writeInt(localMap.size());
        for (int i = 0; i < localMap.size(); i++)
        {
          String str1 = this.headerFields.getKey(i);
          if (null == str1)
            str1 = "<null>";
          String str2 = this.headerFields.getValue(i);
          if (str2 == null)
          {
            if (Cache.DEBUG)
              Trace.print("Header field '" + str1 + "' null, skip.");
          }
          else
          {
            paramDataOutputStream.writeUTF(str1);
            paramDataOutputStream.writeUTF(str2);
          }
        }
      }
      else
      {
        paramDataOutputStream.writeInt(0);
      }
    }
    finally
    {
    }
  }

  private String printManifest()
  {
    Manifest localManifest = getManifest();
    if (localManifest != null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      Attributes localAttributes = localManifest.getMainAttributes();
      Iterator localIterator = localAttributes.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        localStringBuffer.append("key: " + localObject);
        localStringBuffer.append(" value: " + localAttributes.get(localObject) + "\n");
      }
      return localStringBuffer.toString();
    }
    return null;
  }

  public String toString()
  {
    return "URL: " + this.url + " | " + this.indexFile;
  }

  public String dumpToString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("-----Cache Entry------\n");
    localStringBuffer.append("busy: " + getBusy() + "\n");
    localStringBuffer.append("incomplete: " + getIncomplete() + "\n");
    localStringBuffer.append("cacheVersion: " + getCacheVersion() + "\n");
    localStringBuffer.append("contentLength: " + getContentLength() + "\n");
    long l = getLastModified();
    localStringBuffer.append("lastModified: " + l + " [" + new Date(l).toString() + "]\n");
    localStringBuffer.append("expirationDate: " + getExpirationDate() + "\n");
    localStringBuffer.append("version: " + getVersion() + "\n");
    localStringBuffer.append("URL: " + this.url + "\n");
    localStringBuffer.append("NamespaceID: " + getNamespaceID() + "\n");
    localStringBuffer.append("HTTP/HTTPS Header: " + getHeaders() + "\n");
    if (getManifest() != null)
    {
      localStringBuffer.append("Jar-Manifest Main Attributes:\n");
      localStringBuffer.append(printManifest());
      localStringBuffer.append("----------------------\n");
    }
    return localStringBuffer.toString();
  }

  public boolean hasCompressEncoding()
  {
    return HttpUtils.hasGzipOrPack200Encoding(getHeaders());
  }

  boolean matchesVersionString(String paramString, boolean paramBoolean)
  {
    return matchesVersionString(this, paramString, paramBoolean);
  }

  static boolean matchesVersionString(Resource paramResource, String paramString, boolean paramBoolean)
  {
    if ((paramResource.getVersion() == null) && (paramString == null))
      return true;
    VersionString localVersionString = new VersionString(paramString);
    if ((paramBoolean) && (!localVersionString.isSimpleVersion()))
      return false;
    return localVersionString.contains(paramResource.getVersion());
  }

  public boolean isSameEntry(CacheEntry paramCacheEntry)
  {
    return (paramCacheEntry != null) && (getIndexFile().equals(paramCacheEntry.getIndexFile()));
  }

  private void readSection2(RandomAccessFile paramRandomAccessFile)
    throws IOException
  {
    if (this.section2Length > 0)
    {
      byte[] arrayOfByte = new byte[this.section2Length];
      paramRandomAccessFile.read(arrayOfByte);
      DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
      setVersion(localDataInputStream.readUTF());
      setURL(localDataInputStream.readUTF());
      setNamespaceID(localDataInputStream.readUTF());
      setCodebaseIP(localDataInputStream.readUTF());
      readHeaders(localDataInputStream);
    }
  }

  private void readSection1Remaining(DataInputStream paramDataInputStream)
    throws IOException
  {
    setIsShortcutImage(paramDataInputStream.readByte());
    setContentLength(paramDataInputStream.readInt());
    setLastModified(paramDataInputStream.readLong());
    setExpirationDate(paramDataInputStream.readLong());
    this.validationTimestampt = paramDataInputStream.readLong();
    this.knownToBeSigned = (paramDataInputStream.readByte() == 1);
    this.section2Length = paramDataInputStream.readInt();
    this.section3Length = paramDataInputStream.readInt();
    this.section4Length = paramDataInputStream.readInt();
    this.section5Length = paramDataInputStream.readInt();
    this.blacklistValidationTime = paramDataInputStream.readLong();
    this.certExpirationDate = paramDataInputStream.readLong();
    this.classVerificationStatus = paramDataInputStream.readByte();
    this.reducedManifestLength = paramDataInputStream.readInt();
    this.section4Pre15Length = paramDataInputStream.readInt();
    this.hasOnlySignedEntries = (paramDataInputStream.readByte() == 1);
    this.hasSingleCodeSource = (paramDataInputStream.readByte() == 1);
    this.section4CertsLength = paramDataInputStream.readInt();
    this.section4SignersLength = paramDataInputStream.readInt();
    this.hasMissingSignedEntries = (paramDataInputStream.readByte() == 1);
    this.trustedLibrariesValidationTime = paramDataInputStream.readLong();
    this.reducedManifest2Length = paramDataInputStream.readInt();
    setIsProxied(paramDataInputStream.readByte());
    paramDataInputStream.close();
  }

  private void readSection1Remaining604(DataInputStream paramDataInputStream)
    throws IOException
  {
    paramDataInputStream.readByte();
    paramDataInputStream.readByte();
    setIsShortcutImage(paramDataInputStream.readByte());
    setContentLength(paramDataInputStream.readInt());
    setLastModified(paramDataInputStream.readLong());
    setExpirationDate(paramDataInputStream.readLong());
    this.validationTimestampt = paramDataInputStream.readLong();
    this.knownToBeSigned = (paramDataInputStream.readByte() == 1);
    this.section2Length = paramDataInputStream.readInt();
    this.section3Length = paramDataInputStream.readInt();
    this.section4Length = paramDataInputStream.readInt();
    this.section5Length = paramDataInputStream.readInt();
    this.blacklistValidationTime = paramDataInputStream.readLong();
    this.certExpirationDate = paramDataInputStream.readLong();
    this.classVerificationStatus = paramDataInputStream.readByte();
    this.reducedManifestLength = paramDataInputStream.readInt();
    this.section4Pre15Length = paramDataInputStream.readInt();
    this.hasOnlySignedEntries = (paramDataInputStream.readByte() == 1);
    this.hasSingleCodeSource = (paramDataInputStream.readByte() == 1);
    this.section4CertsLength = paramDataInputStream.readInt();
    this.section4SignersLength = paramDataInputStream.readInt();
    this.hasMissingSignedEntries = (paramDataInputStream.readByte() == 1);
    this.trustedLibrariesValidationTime = paramDataInputStream.readLong();
    this.reducedManifest2Length = paramDataInputStream.readInt();
  }

  private void saveUpgrade()
    throws IOException
  {
    Trace.println("Trying to update in place " + this.indexFile.getAbsolutePath(), TraceLevel.CACHE);
    this.cacheVersion = Cache.getCacheVersion();
    boolean bool = isValidEntry();
    try
    {
      setBusy(BUSY_TRUE);
      updateIndexHeaderOnDisk();
      if (bool)
      {
        Trace.println("Upgrade writing to disk for " + getResourceFilename(), TraceLevel.CACHE);
        writeFileToDisk(1, null, false);
        Trace.println("Upgrade of entry done", TraceLevel.CACHE);
      }
      else
      {
        Trace.println("Upgrade of incomplete entry done", TraceLevel.CACHE);
      }
    }
    finally
    {
      setBusy(BUSY_FALSE);
    }
  }

  public int getState()
  {
    if ((this.incomplete == INCOMPLETE_TRUE) && (this.busy == BUSY_FALSE))
      return 2;
    if ((this.incomplete == INCOMPLETE_FALSE) && (this.busy == BUSY_FALSE))
      return 1;
    return 0;
  }

  private CodeSource createCodeSource(Certificate[] paramArrayOfCertificate)
  {
    if (this._url == null)
      try
      {
        this._url = new URL(this.url);
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    return new CodeSource(this._url, paramArrayOfCertificate);
  }

  private CodeSource createCodeSource(CodeSigner[] paramArrayOfCodeSigner)
  {
    if (this._url == null)
      try
      {
        this._url = new URL(this.url);
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    return new CodeSource(this._url, paramArrayOfCodeSigner);
  }

  static
  {
    try
    {
      JavaUtilJarAccess localJavaUtilJarAccess = SharedSecrets.javaUtilJarAccess();
      localJavaUtilJarAccess.setEagerValidation((JarFile)null, false);
      enhancedJarAccess = true;
    }
    catch (NoClassDefFoundError localNoClassDefFoundError)
    {
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
    }
    catch (NullPointerException localNullPointerException)
    {
      enhancedJarAccess = true;
    }
    catch (Exception localException)
    {
    }
    catch (Error localError)
    {
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.CacheEntry
 * JD-Core Version:    0.6.2
 */