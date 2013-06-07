package com.sun.java.browser.net;

public abstract interface ProxyInfo
{
  public abstract String getHost();

  public abstract int getPort();

  public abstract boolean isSocks();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.browser.net.ProxyInfo
 * JD-Core Version:    0.6.2
 */