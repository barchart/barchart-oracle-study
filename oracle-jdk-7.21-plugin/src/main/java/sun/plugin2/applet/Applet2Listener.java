package sun.plugin2.applet;

import com.sun.javaws.exceptions.ExitException;

public abstract interface Applet2Listener
{
  public abstract boolean appletSSVValidation(Plugin2Manager paramPlugin2Manager)
    throws ExitException;

  public abstract void appletJRERelaunch(Plugin2Manager paramPlugin2Manager, String paramString1, String paramString2);

  public abstract boolean isAppletRelaunchSupported();

  public abstract void appletLoaded(Plugin2Manager paramPlugin2Manager);

  public abstract void appletReady(Plugin2Manager paramPlugin2Manager);

  public abstract void appletErrorOccurred(Plugin2Manager paramPlugin2Manager);

  public abstract String getBestJREVersion(Plugin2Manager paramPlugin2Manager, String paramString1, String paramString2);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2Listener
 * JD-Core Version:    0.6.2
 */