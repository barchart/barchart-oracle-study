package sun.plugin.viewer.context;

import java.applet.AppletContext;
import sun.applet.AppletPanel;
import sun.plugin.javascript.JSContext;

public abstract interface PluginAppletContext extends AppletContext, JSContext
{
  public abstract void addAppletPanelInContext(AppletPanel paramAppletPanel);

  public abstract void removeAppletPanelFromContext(AppletPanel paramAppletPanel);

  public abstract void setAppletContextHandle(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.PluginAppletContext
 * JD-Core Version:    0.6.2
 */