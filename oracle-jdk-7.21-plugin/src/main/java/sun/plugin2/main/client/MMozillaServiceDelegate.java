package sun.plugin2.main.client;

import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.services.MPlatformService;

public class MMozillaServiceDelegate extends MozillaServiceDelegate
{
  private MPlatformService service = new MPlatformService();

  public BrowserProxyConfig getProxyConfig()
  {
    return this.service.getProxyConfig();
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return this.service.getSystemProxyHandler();
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return this.service.getAutoProxyHandler();
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return this.service.getBrowserProxyHandler();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.MMozillaServiceDelegate
 * JD-Core Version:    0.6.2
 */