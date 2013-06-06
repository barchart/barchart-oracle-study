package com.sun.deploy.net.cookie;

import java.net.URL;

public abstract interface CookieHandler
{
  public abstract String getCookieInfo(URL paramURL)
    throws CookieUnavailableException;

  public abstract void setCookieInfo(URL paramURL, String paramString)
    throws CookieUnavailableException;
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.cookie.CookieHandler
 * JD-Core Version:    0.6.2
 */