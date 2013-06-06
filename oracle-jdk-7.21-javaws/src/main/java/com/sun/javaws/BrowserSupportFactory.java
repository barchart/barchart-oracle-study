package com.sun.javaws;

public class BrowserSupportFactory
{
  public static BrowserSupport newInstance()
  {
    return new UnixBrowserSupport();
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.BrowserSupportFactory
 * JD-Core Version:    0.6.2
 */