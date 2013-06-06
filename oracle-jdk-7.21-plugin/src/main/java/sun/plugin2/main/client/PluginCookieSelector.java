package sun.plugin2.main.client;

import com.sun.deploy.net.cookie.CookieUnavailableException;
import com.sun.deploy.net.cookie.DeployCookieSelector;
import java.net.CookieHandler;
import java.net.URL;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.util.SystemUtil;

public class PluginCookieSelector extends DeployCookieSelector
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;

  public static void initialize()
  {
    CookieHandler.setDefault(new PluginCookieSelector());
  }

  protected void initializeImpl()
  {
  }

  protected void setCookieInBrowser(URL paramURL, String paramString)
    throws CookieUnavailableException
  {
    Applet2ExecutionContext localApplet2ExecutionContext = Plugin2Manager.getCurrentAppletExecutionContext();
    if (localApplet2ExecutionContext != null)
      localApplet2ExecutionContext.setCookie(paramURL, paramString);
  }

  protected String getCookieFromBrowser(URL paramURL)
    throws CookieUnavailableException
  {
    Applet2ExecutionContext localApplet2ExecutionContext = Plugin2Manager.getCurrentAppletExecutionContext();
    if (localApplet2ExecutionContext != null)
      return localApplet2ExecutionContext.getCookie(paramURL);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.PluginCookieSelector
 * JD-Core Version:    0.6.2
 */