package sun.plugin2.applet2;

import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.impl.awt.AppletCompatibleHost;
import com.sun.javaws.exceptions.ExitException;
import java.net.URL;
import java.util.Enumeration;
import netscape.javascript.JSObject;
import sun.plugin.javascript.JSContext;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Plugin2Manager;

public class Plugin2Host
  implements AppletCompatibleHost, JSContext
{
  private final Plugin2Manager plugin_mgr;
  private final Applet2ExecutionContext exec_ctx;

  public Plugin2Host(Plugin2Manager paramPlugin2Manager)
  {
    this.plugin_mgr = paramPlugin2Manager;
    this.exec_ctx = paramPlugin2Manager.getAppletExecutionContext();
  }

  public URL getDocumentBase()
  {
    return this.plugin_mgr.getDocumentBase();
  }

  public void showDocument(URL paramURL)
  {
    showDocument(paramURL, null);
  }

  public void showDocument(URL paramURL, String paramString)
  {
    if (null == paramString)
      this.exec_ctx.showDocument(paramURL);
    else
      this.exec_ctx.showDocument(paramURL, paramString);
  }

  public void showStatus(String paramString)
  {
    this.exec_ctx.showStatus(paramString);
  }

  public Applet2Adapter getApplet2Adapter(String paramString)
  {
    return this.plugin_mgr.getAppletAdapter(paramString);
  }

  public Enumeration getApplet2Adapters()
  {
    return this.plugin_mgr.getAppletAdapters();
  }

  public JSObject getJSObject()
  {
    return this.exec_ctx.getJSObject(this.plugin_mgr);
  }

  public JSObject getOneWayJSObject()
  {
    return this.exec_ctx.getOneWayJSObject(this.plugin_mgr);
  }

  public void showApplet()
  {
    this.plugin_mgr.getApplet2Adapter().doShowApplet();
  }

  public void showError(String paramString, Throwable paramThrowable, boolean paramBoolean)
  {
    if ((paramThrowable instanceof ExitException))
    {
      ExitException localExitException = (ExitException)paramThrowable;
      if ((localExitException.isSilentException()) && (getJSObject() == null))
      {
        System.exit(0);
        return;
      }
      paramThrowable = localExitException.getException();
    }
    this.plugin_mgr.getApplet2Adapter().doShowError(paramString, paramThrowable, paramBoolean);
  }

  public void clearAppletArea()
  {
    this.plugin_mgr.getApplet2Adapter().doClearAppletArea();
  }

  public void reloadAppletPage()
  {
    this.plugin_mgr.forceReloadApplet();
  }

  public Applet2ExecutionContext getAppletExecutionContext()
  {
    return this.exec_ctx;
  }

  public Object getWindow()
  {
    return getJSObject();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet2.Plugin2Host
 * JD-Core Version:    0.6.2
 */