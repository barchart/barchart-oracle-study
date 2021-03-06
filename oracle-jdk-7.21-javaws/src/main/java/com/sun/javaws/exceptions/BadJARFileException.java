package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;

public class BadJARFileException extends DownloadException
{
  public BadJARFileException(URL paramURL, String paramString, Exception paramException)
  {
    super(null, paramURL, paramString, paramException);
  }

  public String getRealMessage()
  {
    return ResourceManager.getString("launch.error.badjarfile", getResourceString());
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.exceptions.BadJARFileException
 * JD-Core Version:    0.6.2
 */