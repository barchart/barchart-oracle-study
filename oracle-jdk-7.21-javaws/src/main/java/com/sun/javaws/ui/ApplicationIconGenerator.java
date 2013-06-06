package com.sun.javaws.ui;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.Config;
import com.sun.deploy.trace.Trace;
import com.sun.javaws.IconUtil;
import com.sun.javaws.jnl.LaunchDesc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationIconGenerator extends Thread
{
  private File _index;
  private File _dir;
  private final String _key;
  private final LaunchDesc _ld;
  private Properties _props = new Properties();

  public static void generate(LaunchDesc paramLaunchDesc, boolean paramBoolean)
  {
    if (!Cache.isCacheEnabled())
      return;
    if (paramLaunchDesc.isApplicationDescriptor())
    {
      ApplicationIconGenerator localApplicationIconGenerator = new ApplicationIconGenerator(paramLaunchDesc);
      if ((paramBoolean) || (localApplicationIconGenerator.needsCustomIcon()))
        localApplicationIconGenerator.start();
    }
  }

  public static void removeCustomIcon(LaunchDesc paramLaunchDesc)
  {
    if (paramLaunchDesc.isApplicationDescriptor())
    {
      ApplicationIconGenerator localApplicationIconGenerator = new ApplicationIconGenerator(paramLaunchDesc);
      localApplicationIconGenerator.remove();
    }
  }

  public ApplicationIconGenerator(LaunchDesc paramLaunchDesc)
  {
    this._ld = paramLaunchDesc;
    this._dir = new File(Config.getAppIconDir());
    this._key = this._ld.getSplashCanonicalHome().toString();
    String str = Config.getAppIconIndex();
    this._index = new File(str);
    Config.setAppIconCache();
    Config.get().storeIfNeeded();
    if (this._index.exists())
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(this._index);
        if (localFileInputStream != null)
        {
          this._props.load(localFileInputStream);
          localFileInputStream.close();
        }
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
  }

  public boolean needsCustomIcon()
  {
    return !this._props.containsKey(this._key);
  }

  public void remove()
  {
    addAppIconToCacheIndex(this._key, null);
  }

  public void run()
  {
    if ((!this._dir.getParentFile().canWrite()) || ((this._dir.exists()) && (!this._dir.canWrite())) || ((this._index.exists()) && (!this._index.canWrite())))
      return;
    try
    {
      this._dir.mkdirs();
    }
    catch (Throwable localThrowable1)
    {
      appIconError(localThrowable1);
    }
    try
    {
      this._index.createNewFile();
    }
    catch (Throwable localThrowable2)
    {
      appIconError(localThrowable2);
    }
    String str = IconUtil.getIconPath(this._ld);
    if (str == null)
      return;
    try
    {
      addAppIconToCacheIndex(this._key, str);
    }
    catch (Throwable localThrowable3)
    {
      Trace.ignored(localThrowable3);
    }
  }

  private void addAppIconToCacheIndex(String paramString1, String paramString2)
  {
    if (paramString2 != null)
      this._props.setProperty(paramString1, paramString2);
    else if (this._props.containsKey(paramString1))
      this._props.remove(paramString1);
    File[] arrayOfFile = this._dir.listFiles();
    if (arrayOfFile == null)
      return;
    for (int i = 0; i < arrayOfFile.length; i++)
      if (!arrayOfFile[i].equals(this._index))
        try
        {
          String str = arrayOfFile[i].getCanonicalPath();
          if (!this._props.containsValue(str))
            arrayOfFile[i].delete();
        }
        catch (IOException localIOException2)
        {
          appIconError(localIOException2);
        }
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(this._index);
      this._props.store(localFileOutputStream, "");
      localFileOutputStream.flush();
      localFileOutputStream.close();
    }
    catch (IOException localIOException1)
    {
      appIconError(localIOException1);
    }
  }

  private void appIconError(Throwable paramThrowable)
  {
    LaunchErrorDialog.show(null, paramThrowable, false);
    throw new Error(paramThrowable.toString());
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.ui.ApplicationIconGenerator
 * JD-Core Version:    0.6.2
 */