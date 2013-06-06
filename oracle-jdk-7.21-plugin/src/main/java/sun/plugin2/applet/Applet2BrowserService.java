package sun.plugin2.applet;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import com.sun.deploy.services.ServiceManager;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.SecureRandom;
import sun.plugin.services.BrowserService;

public class Applet2BrowserService
  implements BrowserService
{
  private Applet2ExecutionContext defaultContext;

  public static void install(Applet2ExecutionContext paramApplet2ExecutionContext)
  {
    Applet2BrowserService localApplet2BrowserService = new Applet2BrowserService(paramApplet2ExecutionContext);
    try
    {
      Class localClass = ServiceManager.class;
      Field localField = localClass.getDeclaredField("service");
      localField.setAccessible(true);
      localField.set(null, localApplet2BrowserService);
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  public CookieHandler getCookieHandler()
  {
    return null;
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return getContext().getProxyConfig();
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return getContext().getSystemProxyHandler();
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return getContext().getAutoProxyHandler();
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return getContext().getBrowserProxyHandler();
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    return getContext().getBrowserSigningRootCertStore();
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    return getContext().getBrowserSSLRootCertStore();
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return getContext().getBrowserTrustedCertStore();
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    return getContext().getBrowserClientAuthKeyStore();
  }

  public CredentialManager getCredentialManager()
  {
    return getContext().getCredentialManager();
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return getContext().getBrowserAuthenticator();
  }

  public SecureRandom getSecureRandom()
  {
    Applet2ExecutionContext localApplet2ExecutionContext = getContext();
    if (localApplet2ExecutionContext != null)
      return localApplet2ExecutionContext.getSecureRandom();
    return new SecureRandom();
  }

  public boolean isIExplorer()
  {
    return getContext().isIExplorer();
  }

  public boolean isNetscape()
  {
    return getContext().isNetscape();
  }

  public OfflineHandler getOfflineHandler()
  {
    Applet2ExecutionContext localApplet2ExecutionContext = getContext();
    if (localApplet2ExecutionContext == null)
      return null;
    return localApplet2ExecutionContext.getOfflineHandler();
  }

  public Object getAppletContext()
  {
    throw new RuntimeException("Not supported in the new plugin implementation");
  }

  public Object getBeansContext()
  {
    throw new RuntimeException("Not supported in the new plugin implementation");
  }

  public float getBrowserVersion()
  {
    return getContext().getBrowserVersion();
  }

  public boolean isConsoleIconifiedOnClose()
  {
    Applet2ExecutionContext localApplet2ExecutionContext = getContext();
    if (localApplet2ExecutionContext == null)
      return false;
    return localApplet2ExecutionContext.isConsoleIconifiedOnClose();
  }

  public boolean installBrowserEventListener()
  {
    return getContext().installBrowserEventListener();
  }

  public String mapBrowserElement(String paramString)
  {
    return getContext().mapBrowserElement(paramString);
  }

  private Applet2BrowserService(Applet2ExecutionContext paramApplet2ExecutionContext)
  {
    this.defaultContext = paramApplet2ExecutionContext;
  }

  private Applet2ExecutionContext getContext()
  {
    Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
    if (localPlugin2Manager == null)
      return this.defaultContext;
    return localPlugin2Manager.getAppletExecutionContext();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2BrowserService
 * JD-Core Version:    0.6.2
 */