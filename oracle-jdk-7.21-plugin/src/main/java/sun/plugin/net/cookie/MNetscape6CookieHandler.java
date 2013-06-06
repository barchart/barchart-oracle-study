package sun.plugin.net.cookie;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import java.net.URL;
import sun.plugin.navig.motif.Worker;
import sun.plugin.viewer.AppletPanelCache;

public final class MNetscape6CookieHandler
  implements CookieHandler
{
  public void setCookieInfo(URL paramURL, String paramString)
    throws CookieUnavailableException
  {
    if (!AppletPanelCache.hasValidInstance())
      throw new CookieUnavailableException("Cookie service is not available for " + paramURL);
    Worker.setCookieForURL(paramURL.toString(), paramString);
  }

  public String getCookieInfo(URL paramURL)
    throws CookieUnavailableException
  {
    if (!AppletPanelCache.hasValidInstance())
      throw new CookieUnavailableException("Cookie service is not available for " + paramURL);
    return Worker.findCookieForURL(paramURL.toString());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.net.cookie.MNetscape6CookieHandler
 * JD-Core Version:    0.6.2
 */