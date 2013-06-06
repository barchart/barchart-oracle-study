package sun.plugin2.main.client;

import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.DragContext;
import com.sun.deploy.uitoolkit.Window;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import sun.plugin2.applet.Plugin2Manager;

public class Applet2DragContext
  implements DragContext
{
  private static final WeakHashMap store = new WeakHashMap();
  private final WeakReference managerRef;

  private Applet2DragContext(Plugin2Manager paramPlugin2Manager)
  {
    this.managerRef = new WeakReference(paramPlugin2Manager);
  }

  Plugin2Manager getManager()
  {
    return (Plugin2Manager)this.managerRef.get();
  }

  public static synchronized Applet2DragContext getDragContext(Plugin2Manager paramPlugin2Manager)
  {
    Applet2DragContext localApplet2DragContext = (Applet2DragContext)store.get(paramPlugin2Manager);
    if (null == localApplet2DragContext)
    {
      localApplet2DragContext = new Applet2DragContext(paramPlugin2Manager);
      store.put(paramPlugin2Manager, localApplet2DragContext);
    }
    return localApplet2DragContext;
  }

  public Integer getAppletId()
  {
    return getManager().getAppletID();
  }

  public Applet2Adapter getApplet2Adapter()
  {
    return getManager().getApplet2Adapter();
  }

  public int getModalityLevel()
  {
    return getManager().getModalityLevel();
  }

  public Window getParentContainer()
  {
    return getManager().getAppletParent();
  }

  public String getDraggedTitle()
  {
    return getManager().getDraggedTitle();
  }

  public boolean getUndecorated()
  {
    return getManager().getUndecorated();
  }

  public boolean isDisconnected()
  {
    return getManager().isDisconnected();
  }

  public boolean isSignedApplet()
  {
    return getManager().isTrustedApplet();
  }

  public void setDraggedApplet()
  {
    getManager().setDraggedApplet();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.Applet2DragContext
 * JD-Core Version:    0.6.2
 */