package com.sun.deploy.uitoolkit.impl.text;

import com.sun.deploy.uitoolkit.DelegatingPluginUIToolkit;
import com.sun.deploy.uitoolkit.DragContext;
import com.sun.deploy.uitoolkit.DragHelper;
import com.sun.deploy.uitoolkit.DragListener;
import com.sun.deploy.uitoolkit.PluginWindowFactory;
import com.sun.deploy.uitoolkit.Window;
import com.sun.deploy.uitoolkit.WindowFactory;
import com.sun.deploy.uitoolkit.ui.PluginUIFactory;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.message.Pipe;

public class TextPluginUIToolkit extends DelegatingPluginUIToolkit
{
  PluginWindowFactory piWindowFactory = null;
  PluginUIFactory piUIFactory = null;
  public static final DragHelper noOpDragHelper = new DragHelper()
  {
    public void register(DragContext paramAnonymousDragContext, DragListener paramAnonymousDragListener)
    {
    }

    public void makeDisconnected(DragContext paramAnonymousDragContext, Window paramAnonymousWindow)
    {
    }

    public void restore(DragContext paramAnonymousDragContext)
    {
    }

    public void unregister(DragContext paramAnonymousDragContext)
    {
    }
  };

  public TextPluginUIToolkit()
  {
    super(new TextUIToolkit());
  }

  public WindowFactory getWindowFactory()
  {
    if (this.piWindowFactory == null)
      this.piWindowFactory = new TextPluginWindowFactory();
    return this.piWindowFactory;
  }

  public UIFactory getUIFactory()
  {
    if (this.piUIFactory == null)
      this.piUIFactory = new TextPluginUIFactory();
    return this.piUIFactory;
  }

  public boolean printApplet(Plugin2Manager paramPlugin2Manager, int paramInt1, Pipe paramPipe, long paramLong, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    return false;
  }

  public DragHelper getDragHelper()
  {
    return noOpDragHelper;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.text.TextPluginUIToolkit
 * JD-Core Version:    0.6.2
 */