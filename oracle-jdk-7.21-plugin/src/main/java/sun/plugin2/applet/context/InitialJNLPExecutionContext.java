package sun.plugin2.applet.context;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CredentialManager;
import com.sun.deploy.services.Service;
import com.sun.deploy.services.ServiceManager;
import java.security.KeyStore;

public class InitialJNLPExecutionContext extends NoopExecutionContext
{
  private final Service initialService = ServiceManager.getService();

  public InitialJNLPExecutionContext(AppletParameters paramAppletParameters)
  {
    super(paramAppletParameters, null);
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    if (this.initialService != null)
      return this.initialService.getBrowserAuthenticator();
    return super.getBrowserAuthenticator();
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    if (this.initialService != null)
      return this.initialService.getBrowserClientAuthKeyStore();
    return super.getBrowserClientAuthKeyStore();
  }

  public CredentialManager getCredentialManager()
  {
    if (this.initialService != null)
      return this.initialService.getCredentialManager();
    return super.getCredentialManager();
  }

  public OfflineHandler getOfflineHandler()
  {
    if (this.initialService != null)
      return this.initialService.getOfflineHandler();
    return super.getOfflineHandler();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    if (this.initialService != null)
      return this.initialService.getProxyConfig();
    return super.getProxyConfig();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.context.InitialJNLPExecutionContext
 * JD-Core Version:    0.6.2
 */