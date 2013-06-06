package com.sun.deploy.util;

import com.sun.deploy.config.Config;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import sun.misc.BASE64Decoder;
import sun.misc.JavaUtilJarAccess;
import sun.misc.SharedSecrets;
import sun.security.action.OpenFileInputStreamAction;
import sun.security.util.BitArray;

public final class BlackList
{
  private static BlackList INSTANCE = null;
  private static final String DIGEST_MANIFEST = "-DIGEST-MANIFEST";
  private static final String CACHE_VERSION = "v2";
  private static final int BITLEN = 65536;
  private static final int WORDLEN = 2;
  private static final int NEED_CREATE = 0;
  private static final int NEED_LOAD = 1;
  private static final int IN_MEMORY = 2;
  private static final String[] stateStrings = { "NEED_CREATE", "NEED_LOAD", "IN_MEMORY" };
  private List rawBlacklistFiles;
  private Cache cachedBlacklistFile;
  private SmartBitArray cache;
  private long lastModified;
  private boolean isEmpty;
  private int state;

  private static BlackList createDefaultInstance()
  {
    BlackList localBlackList = null;
    if ((Config.getBooleanProperty("deployment.security.blacklist.check")) && (Config.isJavaVersionAtLeast14()) && (Config.checkClassName("sun.security.action.OpenFileInputStreamAction")))
    {
      Trace.msgSecurityPrintln("downloadengine.check.blacklist.enabled");
      localBlackList = new BlackList(false);
    }
    if (localBlackList == null)
      localBlackList = new BlackList(true);
    return localBlackList;
  }

  public static synchronized BlackList getInstance()
  {
    if (INSTANCE == null)
      INSTANCE = createDefaultInstance();
    return INSTANCE;
  }

  static String getCachePath()
  {
    return getInstance().cachedBlacklistFile.file.getPath();
  }

  private BlackList(boolean paramBoolean)
  {
    this.isEmpty = paramBoolean;
    if (paramBoolean)
    {
      this.lastModified = 0L;
    }
    else
    {
      String str1 = Config.getDynamicBlacklistFile();
      String str2 = Config.getSystemBlacklistFile();
      String str3 = Config.getUserBlacklistFile();
      this.rawBlacklistFiles = new ArrayList();
      this.rawBlacklistFiles.add(new File(str1));
      this.rawBlacklistFiles.add(new File(str2));
      this.rawBlacklistFiles.add(new File(str3));
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("v2").append('|');
      Iterator localIterator = this.rawBlacklistFiles.iterator();
      int i = 0;
      while (localIterator.hasNext())
      {
        File localFile = (File)localIterator.next();
        localStringBuilder.append(localFile.getPath()).append('|');
        long l = localFile.lastModified();
        if (l != 0L)
          i = 1;
        localStringBuilder.append(l).append('|');
      }
      if (i == 0)
      {
        this.isEmpty = true;
        this.lastModified = 0L;
        Trace.println("blacklist: no raw file", TraceLevel.SECURITY);
        return;
      }
      this.cachedBlacklistFile = new Cache(localStringBuilder.toString().hashCode());
      if (this.cachedBlacklistFile.exists())
      {
        this.state = 1;
        this.lastModified = this.cachedBlacklistFile.lastModified();
      }
      else
      {
        this.state = 0;
        this.lastModified = new Date().getTime();
      }
    }
    Trace.println("blacklist: created: " + stateStrings[this.state] + ", lastModified: " + this.lastModified, TraceLevel.SECURITY);
  }

  public synchronized boolean contains(String paramString1, String paramString2)
  {
    if (this.isEmpty)
      return false;
    Trace.println("blacklist: check contains " + paramString2 + ", state now " + stateStrings[this.state], TraceLevel.SECURITY);
    if (this.state == 0)
      return checkInRaw(paramString1, paramString2, true);
    try
    {
      if (this.state == 1)
      {
        this.cache = this.cachedBlacklistFile.loadCache();
        this.state = 2;
      }
    }
    catch (IOException localIOException)
    {
      return checkInRaw(paramString1, paramString2, true);
    }
    boolean bool = checkInCache(paramString2);
    Trace.println("blacklist: " + (bool ? "" : "not ") + " found in cache", TraceLevel.SECURITY);
    if (bool)
      return checkInRaw(paramString1, paramString2, false);
    return false;
  }

  public static boolean updateCache()
  {
    BlackList localBlackList = getInstance();
    if (localBlackList.state == 0)
    {
      synchronized (localBlackList)
      {
        localBlackList.checkInRaw(null, null, true);
      }
      return true;
    }
    return false;
  }

  private boolean checkInCache(String paramString)
  {
    byte[] arrayOfByte = debase64(paramString);
    if (arrayOfByte.length == 0)
      return false;
    for (int i = 0; i + 2 <= arrayOfByte.length; i += 2)
    {
      int j = 0;
      for (int k = 0; k < 2; k++)
      {
        j <<= 8;
        j |= arrayOfByte[(i + k)] & 0xFF;
      }
      if (!this.cache.get(j))
        return false;
    }
    return true;
  }

  private boolean checkInRaw(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramBoolean)
      this.cache = new SmartBitArray(65536);
    Iterator localIterator = this.rawBlacklistFiles.iterator();
    boolean bool = false;
    while (localIterator.hasNext())
    {
      File localFile = (File)localIterator.next();
      if (bool)
      {
        checkInOneRaw(localFile, null, null, true);
        Trace.println("blacklist: read raw " + localFile, TraceLevel.SECURITY);
      }
      else
      {
        bool = checkInOneRaw(localFile, paramString1, paramString2, paramBoolean);
        Trace.println("blacklist: check raw " + localFile + ", " + bool, TraceLevel.SECURITY);
      }
      if ((bool) && (!paramBoolean))
        return true;
    }
    if (paramBoolean)
      saveCache();
    return bool;
  }

  private void saveCache()
  {
    if (this.cache.isEmpty())
    {
      this.isEmpty = true;
      Trace.println("blacklist: raw files are all empty", TraceLevel.SECURITY);
      this.cachedBlacklistFile.delete();
    }
    else
    {
      this.cachedBlacklistFile.save(this.cache);
    }
    this.state = 2;
  }

  private boolean checkInOneRaw(File paramFile, String paramString1, String paramString2, boolean paramBoolean)
  {
    if (!paramFile.exists())
      return false;
    BufferedReader localBufferedReader = null;
    boolean bool1 = false;
    try
    {
      FileInputStream localFileInputStream = (FileInputStream)AccessController.doPrivileged(new OpenFileInputStreamAction(paramFile));
      localBufferedReader = new BufferedReader(new InputStreamReader(localFileInputStream));
      StreamTokenizer localStreamTokenizer = new StreamTokenizer(localBufferedReader);
      setupTokenizer(localStreamTokenizer);
      while (true)
      {
        int i = localStreamTokenizer.nextToken();
        if (i == -1)
          break;
        if (i != 10)
        {
          if (i != -3)
            throw new IOException("Unexpected token: " + localStreamTokenizer);
          String str1 = localStreamTokenizer.sval;
          if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST"))
          {
            String str2 = localStreamTokenizer.sval;
            parseColon(localStreamTokenizer);
            String str3 = null;
            localStreamTokenizer.wordChars(61, 61);
            i = localStreamTokenizer.nextToken();
            if (i != -3)
              throw new IOException("Unexpected value: " + localStreamTokenizer);
            localStreamTokenizer.ordinaryChar(61);
            str3 = localStreamTokenizer.sval;
            if (str3 == null)
              throw new IOException("hash must be specified");
            if ((str3.equals(paramString2)) && (str2.equalsIgnoreCase(paramString1)))
            {
              bool1 = true;
              if (!paramBoolean)
              {
                boolean bool2 = bool1;
                return bool2;
              }
            }
            if (paramBoolean)
              encode(str3);
          }
          else
          {
            throw new IOException("Unknown attribute `" + str1 + "', line " + localStreamTokenizer.lineno());
          }
        }
      }
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Trace.println("blacklist: PrivilegedActionException: " + localPrivilegedActionException, TraceLevel.SECURITY);
    }
    catch (IOException localIOException1)
    {
      Trace.println("blacklist: " + localIOException1, TraceLevel.SECURITY);
    }
    finally
    {
      if (localBufferedReader != null)
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException2)
        {
          Trace.println("blacklist: Cannot close reader for " + paramFile, TraceLevel.SECURITY);
        }
    }
    return bool1;
  }

  private void encode(String paramString)
  {
    byte[] arrayOfByte = debase64(paramString);
    for (int i = 0; i + 2 <= arrayOfByte.length; i += 2)
    {
      int j = 0;
      for (int k = 0; k < 2; k++)
      {
        j <<= 8;
        j |= arrayOfByte[(i + k)] & 0xFF;
      }
      this.cache.set(j, true);
    }
  }

  private static byte[] debase64(String paramString)
  {
    try
    {
      return new BASE64Decoder().decodeBuffer(paramString);
    }
    catch (IOException localIOException)
    {
      Trace.println("blacklist: Cannot decode " + paramString, TraceLevel.SECURITY);
    }
    return new byte[0];
  }

  public boolean isEmpty()
  {
    return this.isEmpty;
  }

  private void setupTokenizer(StreamTokenizer paramStreamTokenizer)
  {
    paramStreamTokenizer.resetSyntax();
    paramStreamTokenizer.wordChars(97, 122);
    paramStreamTokenizer.wordChars(65, 90);
    paramStreamTokenizer.wordChars(48, 57);
    paramStreamTokenizer.wordChars(46, 46);
    paramStreamTokenizer.wordChars(45, 45);
    paramStreamTokenizer.wordChars(95, 95);
    paramStreamTokenizer.wordChars(43, 43);
    paramStreamTokenizer.wordChars(47, 47);
    paramStreamTokenizer.whitespaceChars(0, 32);
    paramStreamTokenizer.commentChar(35);
    paramStreamTokenizer.eolIsSignificant(true);
  }

  private void parseColon(StreamTokenizer paramStreamTokenizer)
    throws IOException
  {
    int i = paramStreamTokenizer.nextToken();
    if (i != 58)
      throw new IOException("Expected ':', read " + paramStreamTokenizer);
  }

  private static Attributes readAttributes(JarFile paramJarFile, JarEntry paramJarEntry)
    throws IOException
  {
    InputStream localInputStream = paramJarFile.getInputStream(paramJarEntry);
    try
    {
      Object localObject1 = AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final InputStream val$is;

        public Object run()
          throws Exception
        {
          Attributes localAttributes = new Attributes();
          Class localClass = Class.forName("java.util.jar.Manifest$FastInputStream");
          Constructor[] arrayOfConstructor = localClass.getDeclaredConstructors();
          Constructor localConstructor = null;
          for (int i = 0; i < arrayOfConstructor.length; i++)
          {
            localObject = arrayOfConstructor[i].getParameterTypes();
            if ((localObject.length == 1) && (localObject[0] == InputStream.class))
            {
              localConstructor = arrayOfConstructor[i];
              break;
            }
          }
          if (localConstructor == null)
            throw new Exception("Failed to find stream constructor");
          localConstructor.setAccessible(true);
          Object[] arrayOfObject1 = { this.val$is };
          Object localObject = localConstructor.newInstance(arrayOfObject1);
          byte[] arrayOfByte = new byte[512];
          Class[] arrayOfClass = { localClass, arrayOfByte.getClass() };
          Method localMethod = Attributes.class.getDeclaredMethod("read", arrayOfClass);
          if (localMethod != null)
          {
            localMethod.setAccessible(true);
            Object[] arrayOfObject2 = { localObject, arrayOfByte };
            localMethod.invoke(localAttributes, arrayOfObject2);
          }
          return localAttributes;
        }
      });
      localAttributes = (Attributes)localObject1;
      return localAttributes;
    }
    catch (Exception localException)
    {
      Attributes localAttributes = paramJarEntry.getAttributes();
      return localAttributes;
    }
    finally
    {
      localInputStream.close();
    }
  }

  public boolean checkJarEntry(JarFile paramJarFile, JarEntry paramJarEntry)
    throws IOException, GeneralSecurityException
  {
    if ((isEmpty()) || (paramJarEntry == null))
      return true;
    if (!paramJarEntry.getName().toUpperCase(Locale.ENGLISH).endsWith(".SF"))
      return false;
    Attributes localAttributes = readAttributes(paramJarFile, paramJarEntry);
    if (localAttributes == null)
      return false;
    Iterator localIterator = localAttributes.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = localIterator.next().toString();
      if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST"))
      {
        Attributes.Name localName = new Attributes.Name(str1);
        String str2 = localAttributes.getValue(localName);
        if (INSTANCE.contains(str1, str2))
        {
          Trace.msgSecurityPrintln("downloadengine.check.blacklist.found", new Object[] { paramJarFile.getName() });
          throw new GeneralSecurityException("blacklisted entry!");
        }
      }
    }
    Trace.msgSecurityPrintln("downloadengine.check.blacklist.notfound");
    return false;
  }

  public boolean checkJarFile(JarFile paramJarFile)
    throws IOException
  {
    if (isEmpty())
    {
      Trace.msgSecurityPrintln("downloadengine.check.blacklist.notexist");
      return false;
    }
    List localList = getManifestDigests(paramJarFile);
    Object localObject;
    String str;
    if ((localList != null) && (localList.size() > 0))
      try
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          localObject = (String)localIterator.next();
          str = (String)localIterator.next();
          if (INSTANCE.contains((String)localObject, str))
          {
            Trace.msgSecurityPrintln("downloadengine.check.blacklist.found", new Object[] { paramJarFile.getName() });
            return true;
          }
        }
        return false;
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
      }
    Enumeration localEnumeration = paramJarFile.entries();
    while (localEnumeration.hasMoreElements())
    {
      localObject = (JarEntry)localEnumeration.nextElement();
      str = ((JarEntry)localObject).getName().toUpperCase(Locale.ENGLISH);
      if ((str.startsWith("META-INF/")) || (str.startsWith("/META-INF/")))
        try
        {
          if (checkJarEntry(paramJarFile, (JarEntry)localObject))
            return false;
        }
        catch (GeneralSecurityException localGeneralSecurityException)
        {
          return true;
        }
    }
    Trace.msgSecurityPrintln("downloadengine.check.blacklist.notsigned");
    return false;
  }

  public boolean hasBeenModifiedSince(long paramLong)
  {
    Trace.println("blacklist: hasBeenModifiedSince " + paramLong + " (we have " + this.lastModified + ")", TraceLevel.SECURITY);
    return this.lastModified >= paramLong;
  }

  private static List getManifestDigests(JarFile paramJarFile)
  {
    try
    {
      JavaUtilJarAccess localJavaUtilJarAccess = SharedSecrets.javaUtilJarAccess();
      return localJavaUtilJarAccess.getManifestDigests(paramJarFile);
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      return null;
    }
    catch (NoClassDefFoundError localNoClassDefFoundError)
    {
    }
    return null;
  }

  private static class Cache
  {
    File file = new File(Config.getSecurityCacheDir(), "blacklist.cache");
    int signature;

    Cache(int paramInt)
    {
      this.signature = paramInt;
      FileInputStream localFileInputStream = null;
      int i = 0;
      try
      {
        localFileInputStream = new FileInputStream(this.file);
        if (new DataInputStream(localFileInputStream).readInt() != paramInt)
          i = 1;
      }
      catch (IOException localIOException)
      {
        i = 1;
      }
      finally
      {
        try
        {
          if (localFileInputStream != null)
            localFileInputStream.close();
        }
        catch (Exception localException)
        {
          Trace.println("blacklist: Cannot close " + this.file, TraceLevel.SECURITY);
        }
      }
      if (i != 0)
      {
        Trace.println("blacklist: Reconstruct cache", TraceLevel.SECURITY);
        this.file.delete();
      }
    }

    long lastModified()
    {
      return this.file.lastModified();
    }

    boolean exists()
    {
      return this.file.exists();
    }

    void delete()
    {
      this.file.delete();
    }

    BlackList.SmartBitArray loadCache()
      throws IOException
    {
      Trace.println("blacklist: loadCache", TraceLevel.SECURITY);
      byte[] arrayOfByte = new byte[8192];
      FileInputStream localFileInputStream = null;
      try
      {
        localFileInputStream = new FileInputStream(this.file);
        localFileInputStream.read(arrayOfByte, 0, 4);
        localFileInputStream.read(arrayOfByte);
      }
      finally
      {
        if (localFileInputStream != null)
          localFileInputStream.close();
      }
      return new BlackList.SmartBitArray(65536, arrayOfByte);
    }

    void save(BlackList.SmartBitArray paramSmartBitArray)
    {
      Trace.println("blacklist: save cache to " + this.file, TraceLevel.SECURITY);
      FileOutputStream localFileOutputStream = null;
      try
      {
        new File(Config.getSecurityCacheDir()).mkdir();
        localFileOutputStream = new FileOutputStream(this.file);
        new DataOutputStream(localFileOutputStream).writeInt(this.signature);
        localFileOutputStream.write(paramSmartBitArray.toByteArray());
      }
      catch (IOException localIOException1)
      {
        if (localFileOutputStream != null)
        {
          try
          {
            localFileOutputStream.close();
          }
          catch (IOException localIOException2)
          {
            Trace.println("blacklist: Cannot close " + this.file, TraceLevel.SECURITY);
          }
          localFileOutputStream = null;
        }
        this.file.delete();
        Trace.println("blacklist: Cannot save cache", TraceLevel.SECURITY);
      }
      finally
      {
        if (localFileOutputStream != null)
          try
          {
            localFileOutputStream.close();
          }
          catch (IOException localIOException3)
          {
          }
      }
    }
  }

  private static class SmartBitArray extends BitArray
  {
    private boolean isEmpty = true;

    SmartBitArray(int paramInt)
    {
      super();
    }

    SmartBitArray(int paramInt, byte[] paramArrayOfByte)
    {
      super(paramArrayOfByte);
      this.isEmpty = false;
    }

    public void set(int paramInt, boolean paramBoolean)
      throws ArrayIndexOutOfBoundsException
    {
      super.set(paramInt, paramBoolean);
      this.isEmpty = false;
    }

    boolean isEmpty()
    {
      return this.isEmpty;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.BlackList
 * JD-Core Version:    0.6.2
 */