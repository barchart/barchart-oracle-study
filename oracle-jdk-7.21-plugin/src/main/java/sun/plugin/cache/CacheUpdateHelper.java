package sun.plugin.cache;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.CacheUpdateProgressDialog;
import com.sun.deploy.ui.CacheUpdateProgressDialog.CanceledException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

public class CacheUpdateHelper
{
  private static final String JAVAPI = "javapi";
  private static final String CACHE_VERSION = "v1.0";

  private static String getCacheDirectorySubStructure()
  {
    return "javapi" + File.separator + "v1.0";
  }

  static String getOldCacheDirectoryPath()
  {
    return Config.getCacheDirectory() + File.separator + getCacheDirectorySubStructure();
  }

  public static boolean updateCache()
  {
    String str = getOldCacheDirectoryPath();
    File localFile1 = new File(str);
    File localFile2 = ResourceProvider.get().getCacheDir();
    if ((!localFile1.exists()) || (!localFile1.isDirectory()) || (localFile1.equals(localFile2)))
      return true;
    LinkedList localLinkedList = OldCacheEntry.getEntries();
    ListIterator localListIterator = localLinkedList.listIterator(0);
    int i = localLinkedList.size();
    int j = 0;
    try
    {
      if (i > 0)
        CacheUpdateProgressDialog.showProgress(0, 100);
      Cache.setCleanupEnabled(false);
      while (localListIterator.hasNext())
      {
        OldCacheEntry localOldCacheEntry = (OldCacheEntry)localListIterator.next();
        try
        {
          Cache.insertFile(localOldCacheEntry.getDataFile(), localOldCacheEntry.isJarEntry() ? 256 : 1, localOldCacheEntry.getURL(), localOldCacheEntry.getVersion(), localOldCacheEntry.getLastModified(), localOldCacheEntry.getExpiration());
        }
        catch (IOException localIOException)
        {
          Trace.ignored(localIOException);
        }
        j++;
        CacheUpdateProgressDialog.showProgress(j, i);
      }
    }
    catch (CacheUpdateProgressDialog.CanceledException localCanceledException)
    {
    }
    finally
    {
      CacheUpdateProgressDialog.dismiss();
      Cache.setCleanupEnabled(true);
    }
    return true;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.cache.CacheUpdateHelper
 * JD-Core Version:    0.6.2
 */