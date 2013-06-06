package sun.plugin2.applet;

import com.sun.applet2.Applet2Context;
import com.sun.applet2.Applet2Host;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.jnlp.BasicService;
import sun.plugin2.perf.Plugin2Rollup;

public final class Plugin2BasicService
  implements BasicService
{
  Applet2Context _ac = (Applet2Context)ToolkitStore.get().getAppContext().get("Plugin2CtxKey");
  URL _cb;

  protected Plugin2BasicService(URL paramURL)
  {
    this._cb = paramURL;
  }

  public URL getCodeBase()
  {
    return this._cb;
  }

  public boolean isOffline()
  {
    return DeployOfflineManager.isGlobalOffline();
  }

  public boolean isWebBrowserSupported()
  {
    if ((DeployPerfUtil.isEnabled()) && (DeployPerfUtil.isDeployFirstframePerfEnabled()))
    {
      DeployPerfUtil.put("Plugin2BasicService.isWebBrowserSupported called");
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            DeployPerfUtil.write(new Plugin2Rollup(-1L, -1L));
            Trace.println("completed perf rollup", TraceLevel.BASIC);
          }
          catch (IOException localIOException)
          {
          }
          return null;
        }
      });
    }
    return true;
  }

  public boolean showDocument(URL paramURL)
  {
    if (this._ac != null)
    {
      try
      {
        paramURL = new URL(this._cb, paramURL.toString());
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
      this._ac.getHost().showDocument(paramURL, "_blank");
      return true;
    }
    return false;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Plugin2BasicService
 * JD-Core Version:    0.6.2
 */