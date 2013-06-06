package sun.plugin.net.proxy;

import com.sun.java.browser.net.ProxyInfo;

public class PluginProxyInfo
  implements ProxyInfo
{
  private String host;
  private int port;
  private boolean socks;

  public PluginProxyInfo()
  {
    this.host = null;
    this.port = -1;
    this.socks = false;
  }

  public PluginProxyInfo(String paramString, int paramInt, boolean paramBoolean)
  {
    this.host = paramString;
    this.port = paramInt;
    this.socks = paramBoolean;
  }

  public String getHost()
  {
    return this.host;
  }

  public int getPort()
  {
    return this.port;
  }

  public boolean isSocks()
  {
    return this.socks;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.net.proxy.PluginProxyInfo
 * JD-Core Version:    0.6.2
 */