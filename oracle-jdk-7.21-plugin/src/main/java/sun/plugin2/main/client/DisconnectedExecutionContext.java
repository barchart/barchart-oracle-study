package sun.plugin2.main.client;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.config.Platform;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.DynamicProxyManager;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.URLUtil;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.context.NoopExecutionContext;

public class DisconnectedExecutionContext extends NoopExecutionContext
{
  final Applet2ExecutionContext original;
  private static boolean initializedDynamicProxyManager;

  public DisconnectedExecutionContext(AppletParameters paramAppletParameters, String paramString)
  {
    this(paramAppletParameters, paramString, null);
  }

  public DisconnectedExecutionContext(AppletParameters paramAppletParameters, String paramString, Applet2ExecutionContext paramApplet2ExecutionContext)
  {
    super(paramAppletParameters, paramString);
    this.original = paramApplet2ExecutionContext;
  }

  public void showDocument(URL paramURL)
  {
    URL localURL;
    try
    {
      localURL = new URL(getDocumentBase(null));
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
      return;
    }
    if (!URLUtil.checkTargetURL(localURL, paramURL))
      throw new SecurityException("showDocument url permission denied");
    Platform.get().showDocument(paramURL.toExternalForm());
  }

  public void showDocument(URL paramURL, String paramString)
  {
    showDocument(paramURL);
  }

  public List getProxyList(URL paramURL, boolean paramBoolean)
  {
    initDynamicProxyManager();
    List localList = null;
    try
    {
      localList = DynamicProxyManager.getProxyList(paramURL, paramBoolean);
    }
    catch (Exception localException)
    {
    }
    if (localList != null)
      return localList;
    return super.getProxyList(paramURL, paramBoolean);
  }

  private static synchronized void initDynamicProxyManager()
  {
    if (!initializedDynamicProxyManager)
    {
      initializedDynamicProxyManager = true;
      DynamicProxyManager.reset();
    }
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    if (this.original != null)
      return this.original.getBrowserSigningRootCertStore();
    return super.getBrowserSigningRootCertStore();
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    if (this.original != null)
      return this.original.getBrowserSSLRootCertStore();
    return super.getBrowserSSLRootCertStore();
  }

  public CertStore getBrowserTrustedCertStore()
  {
    if (this.original != null)
      return this.original.getBrowserTrustedCertStore();
    return super.getBrowserTrustedCertStore();
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    if (this.original != null)
      return this.original.getBrowserClientAuthKeyStore();
    return super.getBrowserClientAuthKeyStore();
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    if (this.original != null)
      return this.original.getBrowserAuthenticator();
    return super.getBrowserAuthenticator();
  }

  public CredentialManager getCredentialManager()
  {
    if (this.original != null)
      return this.original.getCredentialManager();
    return super.getCredentialManager();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return ServiceDelegate.get().getProxyConfig();
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return ServiceDelegate.get().getSystemProxyHandler();
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return ServiceDelegate.get().getAutoProxyHandler();
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return ServiceDelegate.get().getBrowserProxyHandler();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.DisconnectedExecutionContext
 * JD-Core Version:    0.6.2
 */