package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.applet2.Applet2Host;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import java.util.Enumeration;

public abstract interface AppletCompatibleHost extends Applet2Host
{
  public abstract void showStatus(String paramString);

  public abstract Applet2Adapter getApplet2Adapter(String paramString);

  public abstract Enumeration getApplet2Adapters();
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AppletCompatibleHost
 * JD-Core Version:    0.6.2
 */