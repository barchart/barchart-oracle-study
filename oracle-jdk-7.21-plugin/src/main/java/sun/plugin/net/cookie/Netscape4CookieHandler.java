package sun.plugin.net.cookie;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import java.net.URL;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.applet.AppletPanel;
import sun.plugin.viewer.AppletPanelCache;
import sun.plugin.viewer.context.PluginAppletContext;

public final class Netscape4CookieHandler
  implements CookieHandler
{
  public synchronized void setCookieInfo(URL paramURL, String paramString)
    throws CookieUnavailableException
  {
    if (!AppletPanelCache.hasValidInstance())
      throw new CookieUnavailableException("Cookie service is not available for " + paramURL);
    PluginAppletContext localPluginAppletContext = getMatchedApplet(paramURL.toString());
    if (null != localPluginAppletContext)
      try
      {
        JSObject localJSObject1 = localPluginAppletContext.getJSObject();
        if (localJSObject1 == null)
          throw new JSException("Unable to obtain Window object");
        JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
        if (localJSObject2 == null)
          throw new JSException("Unable to obtain Document object");
        localJSObject2.setMember("cookie", paramString);
      }
      catch (JSException localJSException)
      {
        localJSException.printStackTrace();
      }
  }

  public synchronized String getCookieInfo(URL paramURL)
    throws CookieUnavailableException
  {
    if (!AppletPanelCache.hasValidInstance())
      throw new CookieUnavailableException("Cookie service is not available for " + paramURL);
    PluginAppletContext localPluginAppletContext = getMatchedApplet(paramURL.toString());
    if (null == localPluginAppletContext)
      return null;
    try
    {
      JSObject localJSObject1 = localPluginAppletContext.getJSObject();
      if (localJSObject1 == null)
        throw new JSException("Unable to obtain Window object");
      JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
      if (localJSObject2 == null)
        throw new JSException("Unable to obtain Document object");
      return (String)localJSObject2.getMember("cookie");
    }
    catch (JSException localJSException)
    {
      localJSException.printStackTrace();
    }
    return null;
  }

  protected PluginAppletContext getMatchedApplet(String paramString)
  {
    String str1 = truncateURL(paramString);
    Object[] arrayOfObject = AppletPanelCache.getAppletPanels();
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      AppletPanel localAppletPanel = (AppletPanel)arrayOfObject[i];
      if (localAppletPanel != null)
      {
        URL localURL = localAppletPanel.getDocumentBase();
        String str2 = truncateURL(localURL.toString());
        if (str1.indexOf(str2) != -1)
          return (PluginAppletContext)localAppletPanel.getAppletContext();
      }
    }
    return null;
  }

  private String truncateURL(String paramString)
  {
    int i = paramString.lastIndexOf('/');
    if (i != -1)
      return paramString.substring(0, i);
    return paramString;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.net.cookie.Netscape4CookieHandler
 * JD-Core Version:    0.6.2
 */