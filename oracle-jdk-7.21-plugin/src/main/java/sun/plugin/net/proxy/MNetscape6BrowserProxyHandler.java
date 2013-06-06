package sun.plugin.net.proxy;

import com.sun.deploy.net.proxy.AbstractBrowserProxyHandler;
import sun.plugin.navig.motif.Worker;

public final class MNetscape6BrowserProxyHandler extends AbstractBrowserProxyHandler
{
  protected String findProxyForURL(String paramString)
  {
    return Worker.getProxySettings(paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.net.proxy.MNetscape6BrowserProxyHandler
 * JD-Core Version:    0.6.2
 */