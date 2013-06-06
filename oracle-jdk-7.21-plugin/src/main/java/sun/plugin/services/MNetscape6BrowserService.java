package sun.plugin.services;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.MNetscape6ProxyConfig;
import com.sun.deploy.net.proxy.MSystemProxyHandler;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.BrowserKeystore;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.MozillaSSLRootCertStore;
import com.sun.deploy.security.MozillaSigningRootCertStore;
import com.sun.deploy.services.MPlatformService;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.util.HashMap;
import sun.plugin.net.cookie.MNetscape6CookieHandler;
import sun.plugin.net.proxy.MNetscape6BrowserProxyHandler;
import sun.plugin.net.proxy.PluginAutoProxyHandler;
import sun.plugin.viewer.context.MNetscape6AppletContext;
import sun.plugin.viewer.context.PluginBeansContext;

public final class MNetscape6BrowserService extends MPlatformService
  implements BrowserService
{
  private static HashMap nameMap = null;

  public CookieHandler getCookieHandler()
  {
    return new MNetscape6CookieHandler();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return new MNetscape6ProxyConfig();
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return new MSystemProxyHandler();
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return new PluginAutoProxyHandler();
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return new MNetscape6BrowserProxyHandler();
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    if (BrowserKeystore.isJSSCryptoConfigured())
      return new MozillaSigningRootCertStore();
    return null;
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    if (BrowserKeystore.isJSSCryptoConfigured())
      return new MozillaSSLRootCertStore();
    return null;
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return null;
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    if (BrowserKeystore.isJSSCryptoConfigured())
    {
      KeyStore localKeyStore = (KeyStore)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            return KeyStore.getInstance("MozillaMy");
          }
          catch (KeyStoreException localKeyStoreException)
          {
            localKeyStoreException.printStackTrace();
          }
          return null;
        }
      });
      return localKeyStore;
    }
    return null;
  }

  public Object getAppletContext()
  {
    return new MNetscape6AppletContext();
  }

  public Object getBeansContext()
  {
    PluginBeansContext localPluginBeansContext = new PluginBeansContext();
    localPluginBeansContext.setPluginAppletContext(new MNetscape6AppletContext());
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
    return 6.0F;
  }

  public boolean isConsoleIconifiedOnClose()
  {
    return false;
  }

  public boolean installBrowserEventListener()
  {
    return true;
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return new MNetscape6BrowserAuthenticator();
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
      nameMap.put("NodeList", "HTMLCollection");
      nameMap.put("HTMLOptionCollection", "HTMLCollection");
      nameMap.put("HTMLInsElement", "HTMLModElement");
      nameMap.put("HTMLDelElement", "HTMLModElement");
      nameMap.put("HTMLSpanElement", "HTMLElement");
    }
    return nameMap;
  }

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.services.MNetscape6BrowserService
 * JD-Core Version:    0.6.2
 */