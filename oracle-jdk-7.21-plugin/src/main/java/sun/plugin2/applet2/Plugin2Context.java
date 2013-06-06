package sun.plugin2.applet2;

import com.sun.applet2.Applet2Context;
import com.sun.applet2.Applet2Host;
import com.sun.applet2.AppletParameters;
import com.sun.applet2.preloader.Preloader;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.util.StringUtils;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Plugin2Manager;

public class Plugin2Context
  implements Applet2Context
{
  private final Plugin2Manager plugin_mgr;
  private Plugin2Host host;
  private Applet2ExecutionContext exec_ctx;
  private AppletParameters appletParameters;

  public Plugin2Context(Plugin2Manager paramPlugin2Manager)
  {
    this.plugin_mgr = paramPlugin2Manager;
  }

  public String getName()
  {
    return this.plugin_mgr.getName();
  }

  public int getHeight()
  {
    this.plugin_mgr.ensureSizeIsValid();
    return this.plugin_mgr.getHeight();
  }

  public int getWidth()
  {
    this.plugin_mgr.ensureSizeIsValid();
    return this.plugin_mgr.getWidth();
  }

  public AppletParameters getParameters()
  {
    if (this.appletParameters == null)
    {
      if (this.exec_ctx == null)
        throw new IllegalStateException("Requires AppletExecutionContext to be set by now");
      this.appletParameters = this.exec_ctx.getAppletParameters();
      if (this.appletParameters == null)
        throw new IllegalStateException("AppletExecutionContext illegally returned a null parameter map");
    }
    return this.appletParameters;
  }

  public String getParameter(String paramString)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    AppletParameters localAppletParameters = getParameters();
    synchronized (localAppletParameters)
    {
      return StringUtils.trimWhitespace((String)localAppletParameters.get(paramString));
    }
  }

  public boolean isActive()
  {
    return this.plugin_mgr.isAppletStarted();
  }

  public URL getCodeBase()
  {
    return this.plugin_mgr.getCodeBase();
  }

  public Applet2Host getHost()
  {
    return this.host;
  }

  public Preloader getPreloader()
  {
    return this.plugin_mgr.getApplet2Adapter().getPreloader();
  }

  public synchronized void setHost(Plugin2Host paramPlugin2Host)
  {
    this.host = paramPlugin2Host;
    this.exec_ctx = paramPlugin2Host.getAppletExecutionContext();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet2.Plugin2Context
 * JD-Core Version:    0.6.2
 */