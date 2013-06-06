package com.sun.javaws.jnl;

import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.javaws.exceptions.FailedDownloadingResourceException;
import com.sun.javaws.exceptions.JNLPException;
import java.io.IOException;
import java.net.URL;

public class JARUpdater
{
  private JARDesc _jar = null;
  private boolean _updateChecked = false;
  private boolean _updateAvailable = false;

  public JARUpdater(JARDesc paramJARDesc)
  {
    this._jar = paramJARDesc;
  }

  public synchronized boolean isUpdateAvailable()
    throws Exception
  {
    if (!this._updateChecked)
    {
      Trace.println("JARUpdater: update check for " + this._jar.getLocation().toString(), TraceLevel.NETWORK);
      try
      {
        this._updateAvailable = updateCheck();
        this._updateChecked = true;
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
        throw localException;
      }
    }
    return this._updateAvailable;
  }

  Resource downloadUpdate()
    throws Exception
  {
    if (isUpdateAvailable())
    {
      Resource localResource = download();
      synchronized (this)
      {
        this._updateAvailable = false;
      }
      return localResource;
    }
    return null;
  }

  private boolean updateCheck()
    throws JNLPException
  {
    URL localURL = this._jar.getLocation();
    String str = this._jar.getVersion();
    boolean bool = false;
    if (str != null)
      return false;
    try
    {
      bool = ResourceProvider.get().isUpdateAvailable(localURL, str, getDownloadType(), null);
    }
    catch (IOException localIOException)
    {
      ResourcesDesc localResourcesDesc = this._jar.getParent();
      LaunchDesc localLaunchDesc = localResourcesDesc == null ? null : localResourcesDesc.getParent();
      throw new FailedDownloadingResourceException(localLaunchDesc, localURL, null, localIOException);
    }
    return bool;
  }

  private Resource download()
    throws JNLPException
  {
    int i = getDownloadType();
    URL localURL = this._jar.getLocation();
    String str = this._jar.getVersion();
    try
    {
      return ResourceProvider.get().downloadUpdate(localURL, str, i, false);
    }
    catch (IOException localIOException)
    {
      throw new FailedDownloadingResourceException(localURL, str, localIOException);
    }
  }

  private int getDownloadType()
  {
    int i = 256;
    if (this._jar.isNativeLib())
      i |= 16;
    if (this._jar.isPack200Enabled())
      i |= 4096;
    return i;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.jnl.JARUpdater
 * JD-Core Version:    0.6.2
 */