package sun.applet;

import java.util.EventListener;

public abstract interface AppletListener extends EventListener
{
  public abstract void appletStateChanged(AppletEvent paramAppletEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.applet.AppletListener
 * JD-Core Version:    0.6.2
 */