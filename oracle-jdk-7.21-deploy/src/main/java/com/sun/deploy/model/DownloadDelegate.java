package com.sun.deploy.model;

import com.sun.applet2.preloader.CancelException;
import java.net.URL;

public abstract interface DownloadDelegate
{
  public abstract void downloadFailed(URL paramURL, String paramString)
    throws CancelException;

  public abstract void downloading(URL paramURL, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
    throws CancelException;

  public abstract void patching(URL paramURL, String paramString, int paramInt)
    throws CancelException;

  public abstract void setTotalSize(long paramLong);

  public abstract void validating(URL paramURL, int paramInt1, int paramInt2)
    throws CancelException;
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.model.DownloadDelegate
 * JD-Core Version:    0.6.2
 */