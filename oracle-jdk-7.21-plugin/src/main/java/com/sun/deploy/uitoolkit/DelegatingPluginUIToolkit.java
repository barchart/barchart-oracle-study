package com.sun.deploy.uitoolkit;

import com.sun.applet2.Applet2Context;
import com.sun.applet2.preloader.Preloader;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.uitoolkit.ui.UIFactory;

public abstract class DelegatingPluginUIToolkit extends PluginUIToolkit
{
  private final UIToolkit tk;

  private DelegatingPluginUIToolkit()
  {
    this.tk = null;
  }

  public DelegatingPluginUIToolkit(UIToolkit paramUIToolkit)
  {
    this.tk = paramUIToolkit;
  }

  public void init()
    throws Exception
  {
    this.tk.init();
  }

  public void dispose()
    throws Exception
  {
    this.tk.dispose();
  }

  public UIFactory getUIFactory()
  {
    return this.tk.getUIFactory();
  }

  public WindowFactory getWindowFactory()
  {
    return this.tk.getWindowFactory();
  }

  public Preloader getDefaultPreloader()
  {
    return this.tk.getDefaultPreloader();
  }

  public boolean isHeadless()
  {
    return this.tk.isHeadless();
  }

  public void setContextClassLoader(ClassLoader paramClassLoader)
  {
    this.tk.setContextClassLoader(paramClassLoader);
  }

  public void warmup()
  {
    this.tk.warmup();
  }

  public UIToolkit changeMode(int paramInt)
  {
    return this.tk.changeMode(paramInt);
  }

  public Applet2Adapter getApplet2Adapter(Applet2Context paramApplet2Context)
  {
    return this.tk.getApplet2Adapter(paramApplet2Context);
  }

  public AppContext getAppContext()
  {
    return this.tk.getAppContext();
  }

  public AppContext createAppContext()
  {
    return this.tk.createAppContext();
  }

  public SecurityManager getSecurityManager()
  {
    return this.tk.getSecurityManager();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.DelegatingPluginUIToolkit
 * JD-Core Version:    0.6.2
 */