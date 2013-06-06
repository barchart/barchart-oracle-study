package com.sun.deploy.net.proxy;

public abstract interface BrowserProxyConfig
{
  public abstract BrowserProxyInfo getBrowserProxyInfo();

  public abstract void getSystemProxy(BrowserProxyInfo paramBrowserProxyInfo);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.proxy.BrowserProxyConfig
 * JD-Core Version:    0.6.2
 */