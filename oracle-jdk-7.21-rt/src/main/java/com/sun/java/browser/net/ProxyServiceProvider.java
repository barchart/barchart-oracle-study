package com.sun.java.browser.net;

import java.net.URL;

public abstract interface ProxyServiceProvider
{
  public abstract ProxyInfo[] getProxyInfo(URL paramURL);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.browser.net.ProxyServiceProvider
 * JD-Core Version:    0.6.2
 */