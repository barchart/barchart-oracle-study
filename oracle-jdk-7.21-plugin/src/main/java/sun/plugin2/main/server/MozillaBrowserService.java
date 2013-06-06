package sun.plugin2.main.server;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.AbstractBrowserProxyHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.BrowserProxyInfo;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.net.proxy.SunAutoProxyHandler;
import com.sun.deploy.security.AbstractBrowserAuthenticator;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import com.sun.deploy.services.Service;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import sun.plugin.services.BrowserService;
import sun.plugin2.util.SystemUtil;

public class MozillaBrowserService
  implements BrowserService
{
  public Object getAppletContext()
  {
    throw new UnsupportedOperationException();
  }

  public Object getBeansContext()
  {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  public boolean isConsoleIconifiedOnClose()
  {
    throw new UnsupportedOperationException();
  }

  public boolean installBrowserEventListener()
  {
    throw new UnsupportedOperationException();
  }

  public String mapBrowserElement(String paramString)
  {
    throw new UnsupportedOperationException();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return new BrowserProxyConfig()
    {
      public BrowserProxyInfo getBrowserProxyInfo()
      {
        BrowserProxyInfo localBrowserProxyInfo = new BrowserProxyInfo();
        localBrowserProxyInfo.setType(3);
        return localBrowserProxyInfo;
      }

      public void getSystemProxy(BrowserProxyInfo paramAnonymousBrowserProxyInfo)
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  public ProxyHandler getSystemProxyHandler()
  {
    int i = SystemUtil.getOSType();
    if (i == 1)
      throw new UnsupportedOperationException();
    try
    {
      return (ProxyHandler)Class.forName("com.sun.deploy.net.proxy.MSystemProxyHandler").newInstance();
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return new MozillaProxyHandler();
  }

  public ProxyHandler getAutoProxyHandler()
  {
    int i = SystemUtil.getOSType();
    try
    {
      if (i == 1)
        return (ProxyHandler)Class.forName("com.sun.deploy.net.proxy.WMozillaAutoProxyHandler").newInstance();
      return new SunAutoProxyHandler();
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public CookieHandler getCookieHandler()
  {
    return new MozillaCookieHandler();
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return new MozillaBrowserAuthenticator();
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

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }

  private Service getPlatformService()
  {
    int i = SystemUtil.getOSType();
    try
    {
      if (i == 1)
        return (Service)Class.forName("com.sun.deploy.services.WPlatformService").newInstance();
      if (i == 2)
        return (Service)Class.forName("com.sun.deploy.services.MPlatformService").newInstance();
      if (i == 3)
        return (Service)Class.forName("com.sun.deploy.services.MacOSXPlatformService").newInstance();
      throw new UnsupportedOperationException();
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public SecureRandom getSecureRandom()
  {
    Service localService = getPlatformService();
    return localService != null ? localService.getSecureRandom() : null;
  }

  public CredentialManager getCredentialManager()
  {
    Service localService = getPlatformService();
    return localService != null ? localService.getCredentialManager() : null;
  }

  class MozillaBrowserAuthenticator extends AbstractBrowserAuthenticator
  {
    MozillaBrowserAuthenticator()
    {
    }

    public PasswordAuthentication getAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, URL paramURL, boolean paramBoolean)
    {
      throw new RuntimeException("Should not call this");
    }
  }

  class MozillaCookieHandler
    implements CookieHandler
  {
    MozillaCookieHandler()
    {
    }

    public void setCookieInfo(URL paramURL, String paramString)
      throws CookieUnavailableException
    {
      throw new CookieUnavailableException("Should not call this");
    }

    public String getCookieInfo(URL paramURL)
      throws CookieUnavailableException
    {
      throw new CookieUnavailableException("Should not call this");
    }
  }

  class MozillaProxyHandler extends AbstractBrowserProxyHandler
  {
    MozillaProxyHandler()
    {
    }

    protected String findProxyForURL(String paramString)
    {
      return ((MozillaPlugin)ProxySupport.getCurrentPlugin()).getProxy(paramString);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.MozillaBrowserService
 * JD-Core Version:    0.6.2
 */