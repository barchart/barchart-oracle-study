package sun.plugin.cache;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class OldCacheEntry
{
  private static final String INDEX_FILE_EXT = ".idx";
  private static final String DATA_FILE_EXT = ".zip";
  private static final String JAR_FILE_EXT = ".jar";
  private static final String JARJAR_FILE_EXT = ".jarjar";
  private static final int JAR = 1;
  private static final byte OLD_CACHE_VERSION = 16;
  private String url;
  private long expiration = 0L;
  private long lastModified = 0L;
  private String version = null;
  private File indexFile = null;
  private File dataFile = null;
  private boolean isJarEntry = false;

  boolean isJarEntry()
  {
    return this.isJarEntry;
  }

  File getDataFile()
  {
    return this.dataFile;
  }

  URL getURL()
  {
    try
    {
      return new URL(this.url);
    }
    catch (MalformedURLException localMalformedURLException)
    {
    }
    return null;
  }

  long getExpiration()
  {
    return this.expiration;
  }

  long getLastModified()
  {
    return this.lastModified;
  }

  String getVersion()
  {
    if ((this.version == null) || (this.version.equals("x.x.x.x")))
      this.version = null;
    return this.version;
  }

  static LinkedList getEntries()
  {
    LinkedList localLinkedList = new LinkedList();
    String str = File.separator;
    File localFile1 = new File(CacheUpdateHelper.getOldCacheDirectoryPath() + str + "jar");
    getFileEntries(localFile1, localLinkedList);
    File localFile2 = new File(CacheUpdateHelper.getOldCacheDirectoryPath() + str + "file");
    getFileEntries(localFile2, localLinkedList);
    return localLinkedList;
  }

  private static void getFileEntries(File paramFile, LinkedList paramLinkedList)
  {
    if (paramFile.exists())
    {
      File[] arrayOfFile = paramFile.listFiles(new FileFilter()
      {
        public boolean accept(File paramAnonymousFile)
        {
          String str = paramAnonymousFile.getName();
          return str.toLowerCase().endsWith(".idx");
        }
      });
      for (int i = 0; i < arrayOfFile.length; i++)
      {
        OldCacheEntry localOldCacheEntry = null;
        try
        {
          localOldCacheEntry = getDetails(arrayOfFile[i]);
        }
        catch (IOException localIOException)
        {
          localOldCacheEntry = null;
        }
        if (localOldCacheEntry != null)
          paramLinkedList.add(localOldCacheEntry);
      }
    }
  }

  private static OldCacheEntry getDetails(File paramFile)
    throws IOException
  {
    OldCacheEntry localOldCacheEntry = null;
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "r");
    try
    {
      if (localRandomAccessFile.readByte() == 16)
      {
        localOldCacheEntry = new OldCacheEntry();
        localOldCacheEntry.indexFile = paramFile;
        localOldCacheEntry.url = localRandomAccessFile.readUTF();
        localOldCacheEntry.lastModified = localRandomAccessFile.readLong();
        localOldCacheEntry.expiration = localRandomAccessFile.readLong();
        int i = localRandomAccessFile.readInt();
        if (i == 1)
        {
          localOldCacheEntry.isJarEntry = true;
          localOldCacheEntry.version = localRandomAccessFile.readUTF();
        }
        File localFile = getDataFileFromIndex(paramFile, localOldCacheEntry.url);
        if (localFile.exists())
          localOldCacheEntry.dataFile = localFile;
        else
          localOldCacheEntry = null;
      }
    }
    finally
    {
      try
      {
        localRandomAccessFile.close();
        localRandomAccessFile = null;
      }
      catch (IOException localIOException)
      {
      }
    }
    return localOldCacheEntry;
  }

  private static final File getDataFileFromIndex(File paramFile, String paramString)
  {
    String str = paramFile.getName();
    str = str.substring(0, str.length() - ".idx".length());
    str = str + getFileExtension(paramString);
    return new File(paramFile.getParentFile(), str);
  }

  private static final String getFileExtension(String paramString)
  {
    String str = "";
    int i = paramString.lastIndexOf('.');
    if (i != -1)
      str = paramString.substring(i);
    if ((str.equalsIgnoreCase(".jar")) || (str.equalsIgnoreCase(".jarjar")))
      str = ".zip";
    return str;
  }

  public String toString()
  {
    return "url: " + getURL() + "\n" + "dataFile: " + getDataFile() + "\n" + "expiration: " + getExpiration() + "\n" + "lastModified: " + getLastModified() + "\n" + "version: " + getVersion() + "\n";
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.cache.OldCacheEntry
 * JD-Core Version:    0.6.2
 */