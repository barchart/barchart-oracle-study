package sun.plugin.services;

import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.services.Service;

public abstract interface BrowserService extends Service
{
  public abstract Object getAppletContext();

  public abstract Object getBeansContext();

  public abstract boolean isIExplorer();

  public abstract boolean isNetscape();

  public abstract float getBrowserVersion();

  public abstract boolean isConsoleIconifiedOnClose();

  public abstract boolean installBrowserEventListener();

  public abstract BrowserAuthenticator getBrowserAuthenticator();

  public abstract String mapBrowserElement(String paramString);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.services.BrowserService
 * JD-Core Version:    0.6.2
 */