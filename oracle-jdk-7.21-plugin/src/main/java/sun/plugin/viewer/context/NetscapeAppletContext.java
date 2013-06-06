package sun.plugin.viewer.context;

import com.sun.deploy.services.ServiceManager;
import sun.plugin.services.BrowserService;

public class NetscapeAppletContext extends DefaultPluginAppletContext
{
  protected int instance = -1;

  public netscape.javascript.JSObject getJSObject()
  {
    if (this.instance < 0)
      return null;
    BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
    if (localBrowserService.getBrowserVersion() >= 5.0F)
    {
      sun.plugin.javascript.navig5.JSObject localJSObject = new sun.plugin.javascript.navig5.JSObject(this.instance);
      localJSObject.setNetscapeAppletContext(this);
      return localJSObject;
    }
    if (localBrowserService.getBrowserVersion() < 4.0F)
      return new sun.plugin.javascript.navig.Window(this.instance);
    return new sun.plugin.javascript.navig4.Window(this.instance);
  }

  public netscape.javascript.JSObject getOneWayJSObject()
  {
    return null;
  }

  public void setAppletContextHandle(int paramInt)
  {
    this.instance = paramInt;
  }

  public int getAppletContextHandle()
  {
    return this.instance;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.NetscapeAppletContext
 * JD-Core Version:    0.6.2
 */