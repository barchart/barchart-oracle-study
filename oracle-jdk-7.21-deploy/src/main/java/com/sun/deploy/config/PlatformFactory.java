package com.sun.deploy.config;

public class PlatformFactory
{
  public static Platform newInstance()
  {
    return new UnixPlatform();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.config.PlatformFactory
 * JD-Core Version:    0.6.2
 */