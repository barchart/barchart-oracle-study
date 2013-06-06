package sun.plugin2.applet;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public abstract interface Applet2ExecutionContext
{
  public abstract AppletParameters getAppletParameters();

  public abstract void setAppletParameters(AppletParameters paramAppletParameters);

  public abstract String getDocumentBase(Plugin2Manager paramPlugin2Manager);

  public abstract List getProxyList(URL paramURL, boolean paramBoolean);

  public abstract void setCookie(URL paramURL, String paramString)
    throws CookieUnavailableException;

  public abstract String getCookie(URL paramURL)
    throws CookieUnavailableException;

  public abstract CertStore getBrowserSigningRootCertStore();

  public abstract CertStore getBrowserSSLRootCertStore();

  public abstract CertStore getBrowserTrustedCertStore();

  public abstract KeyStore getBrowserClientAuthKeyStore();

  public abstract BrowserAuthenticator getBrowserAuthenticator();

  public abstract CredentialManager getCredentialManager();

  public abstract SecureRandom getSecureRandom();

  public abstract boolean isIExplorer();

  public abstract boolean isNetscape();

  public abstract OfflineHandler getOfflineHandler();

  public abstract float getBrowserVersion();

  public abstract boolean isConsoleIconifiedOnClose();

  public abstract boolean installBrowserEventListener();

  public abstract String mapBrowserElement(String paramString);

  public abstract void showDocument(URL paramURL);

  public abstract void showDocument(URL paramURL, String paramString);

  public abstract void showStatus(String paramString);

  public abstract JSObject getJSObject(Plugin2Manager paramPlugin2Manager)
    throws JSException;

  public abstract JSObject getOneWayJSObject(Plugin2Manager paramPlugin2Manager)
    throws JSException;

  public abstract BrowserProxyConfig getProxyConfig();

  public abstract ProxyHandler getSystemProxyHandler();

  public abstract ProxyHandler getAutoProxyHandler();

  public abstract ProxyHandler getBrowserProxyHandler();

  public abstract boolean requestCustomSecurityManager();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2ExecutionContext
 * JD-Core Version:    0.6.2
 */