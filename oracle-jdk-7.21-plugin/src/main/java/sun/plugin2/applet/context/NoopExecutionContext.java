package sun.plugin2.applet.context;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import java.io.PrintStream;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Plugin2Manager;

public class NoopExecutionContext
  implements Applet2ExecutionContext
{
  private AppletParameters params;
  private String documentBase;

  public NoopExecutionContext(AppletParameters paramAppletParameters, String paramString)
  {
    this.params = paramAppletParameters;
    this.documentBase = paramString;
  }

  public AppletParameters getAppletParameters()
  {
    return this.params;
  }

  public void setAppletParameters(AppletParameters paramAppletParameters)
  {
    this.params = paramAppletParameters;
  }

  public String getDocumentBase(Plugin2Manager paramPlugin2Manager)
  {
    return this.documentBase;
  }

  public List getProxyList(URL paramURL, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Proxy.NO_PROXY);
    return localArrayList;
  }

  public void setCookie(URL paramURL, String paramString)
    throws CookieUnavailableException
  {
  }

  public String getCookie(URL paramURL)
    throws CookieUnavailableException
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

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return null;
  }

  public CredentialManager getCredentialManager()
  {
    return null;
  }

  public SecureRandom getSecureRandom()
  {
    return new SecureRandom();
  }

  public boolean isIExplorer()
  {
    return false;
  }

  public boolean isNetscape()
  {
    return false;
  }

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }

  public float getBrowserVersion()
  {
    return 1.0F;
  }

  public boolean isConsoleIconifiedOnClose()
  {
    return false;
  }

  public boolean installBrowserEventListener()
  {
    return false;
  }

  public String mapBrowserElement(String paramString)
  {
    return paramString;
  }

  public void showDocument(URL paramURL)
  {
  }

  public void showDocument(URL paramURL, String paramString)
  {
  }

  public void showStatus(String paramString)
  {
    System.out.println("Applet status: " + paramString);
  }

  public JSObject getJSObject(Plugin2Manager paramPlugin2Manager)
    throws JSException
  {
    return null;
  }

  public JSObject getOneWayJSObject(Plugin2Manager paramPlugin2Manager)
    throws JSException
  {
    return null;
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return null;
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return null;
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return null;
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return null;
  }

  public boolean requestCustomSecurityManager()
  {
    return false;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.context.NoopExecutionContext
 * JD-Core Version:    0.6.2
 */