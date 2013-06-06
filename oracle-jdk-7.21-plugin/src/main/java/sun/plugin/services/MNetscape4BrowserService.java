package sun.plugin.services;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.MNetscape4ProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.services.MPlatformService;
import java.security.KeyStore;
import java.util.HashMap;
import sun.plugin.net.cookie.Netscape4CookieHandler;
import sun.plugin.net.proxy.PluginAutoProxyHandler;
import sun.plugin.viewer.context.NetscapeAppletContext;
import sun.plugin.viewer.context.PluginBeansContext;

public final class MNetscape4BrowserService extends MPlatformService
  implements BrowserService
{
  private static HashMap nameMap = null;

  public CookieHandler getCookieHandler()
  {
    return new Netscape4CookieHandler();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return new MNetscape4ProxyConfig();
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return null;
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return new PluginAutoProxyHandler();
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return null;
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    return null;
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    return null;
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return null;
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    return null;
  }

  public Object getAppletContext()
  {
    return new NetscapeAppletContext();
  }

  public Object getBeansContext()
  {
    PluginBeansContext localPluginBeansContext = new PluginBeansContext();
    localPluginBeansContext.setPluginAppletContext(new NetscapeAppletContext());
    return localPluginBeansContext;
  }

  public boolean isIExplorer()
  {
    return false;
  }

  public boolean isNetscape()
  {
    return true;
  }

  public float getBrowserVersion()
  {
    return 4.0F;
  }

  public boolean isConsoleIconifiedOnClose()
  {
    return true;
  }

  public boolean installBrowserEventListener()
  {
    return true;
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return null;
  }

  public String mapBrowserElement(String paramString)
  {
    String str = (String)getNameMap().get(paramString);
    return str != null ? str : paramString;
  }

  private static synchronized HashMap getNameMap()
  {
    if (nameMap == null)
    {
      nameMap = new HashMap();
      nameMap.put("self.document.forms", "ns4.HTMLFormCollection");
      nameMap.put("self.document.links", "ns4.HTMLAnchorCollection");
      nameMap.put("self.document.images", "ns4.HTMLImageCollection");
      nameMap.put("self.document.applets", "ns4.HTMLAppletCollection");
      nameMap.put("self.document.anchors", "ns4.HTMLAnchorCollection");
    }
    return nameMap;
  }

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.services.MNetscape4BrowserService
 * JD-Core Version:    0.6.2
 */