package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;

public class InvalidJarDiffException extends DownloadException
{
  public InvalidJarDiffException(URL paramURL, String paramString, Exception paramException)
  {
    super(null, paramURL, paramString, paramException);
  }

  public String getRealMessage()
  {
    return ResourceManager.getString("launch.error.invalidjardiff", getResourceString());
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.exceptions.InvalidJarDiffException
 * JD-Core Version:    0.6.2
 */