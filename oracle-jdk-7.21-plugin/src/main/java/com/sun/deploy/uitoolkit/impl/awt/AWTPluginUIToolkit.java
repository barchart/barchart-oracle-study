package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.applet2.Applet2Context;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.DelegatingPluginUIToolkit;
import com.sun.deploy.uitoolkit.DragHelper;
import com.sun.deploy.uitoolkit.PluginWindowFactory;
import com.sun.deploy.uitoolkit.WindowFactory;
import com.sun.deploy.uitoolkit.ui.PluginUIFactory;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.message.Pipe;

public class AWTPluginUIToolkit extends DelegatingPluginUIToolkit
{
  PluginWindowFactory piWindowFactory = null;
  PluginUIFactory piUIFactory = null;

  public AWTPluginUIToolkit()
  {
    super(new UIToolkitImpl());
  }

  public AWTPluginUIToolkit(UIToolkitImpl paramUIToolkitImpl)
  {
    super(paramUIToolkitImpl);
  }

  public WindowFactory getWindowFactory()
  {
    if (this.piWindowFactory == null)
      this.piWindowFactory = new AWTPluginWindowFactory();
    return this.piWindowFactory;
  }

  public Applet2Adapter getApplet2Adapter(Applet2Context paramApplet2Context)
  {
    return new AWTAppletAdapter(paramApplet2Context);
  }

  public UIFactory getUIFactory()
  {
    if (this.piUIFactory == null)
      this.piUIFactory = new AWTPluginUIFactory();
    return this.piUIFactory;
  }

  public boolean printApplet(Plugin2Manager paramPlugin2Manager, int paramInt1, Pipe paramPipe, long paramLong, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    return AWTClientPrintHelper.print(paramPlugin2Manager, paramInt1, paramPipe, paramLong, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5);
  }

  public DragHelper getDragHelper()
  {
    return AWTDragHelper.getInstance();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTPluginUIToolkit
 * JD-Core Version:    0.6.2
 */