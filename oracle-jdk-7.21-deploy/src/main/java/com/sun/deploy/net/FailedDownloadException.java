package com.sun.deploy.net;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;

public class FailedDownloadException extends DownloadException
{
  private boolean _offline = false;

  public FailedDownloadException(URL paramURL, String paramString, Exception paramException)
  {
    super(paramURL, paramString, paramException);
  }

  public FailedDownloadException(URL paramURL, String paramString, Exception paramException, boolean paramBoolean)
  {
    super(paramURL, paramString, paramException);
    this._offline = true;
  }

  public String getRealMessage()
  {
    if (this._offline)
      return ResourceManager.getString("launch.error.offline", getResourceString());
    return ResourceManager.getString("launch.error.failedloadingresource", getResourceString());
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.FailedDownloadException
 * JD-Core Version:    0.6.2
 */