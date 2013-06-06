package com.sun.deploy.uitoolkit;

import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.message.Pipe;

public abstract class PluginUIToolkit extends UIToolkit
{
  public abstract boolean printApplet(Plugin2Manager paramPlugin2Manager, int paramInt1, Pipe paramPipe, long paramLong, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5);

  public abstract DragHelper getDragHelper();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.PluginUIToolkit
 * JD-Core Version:    0.6.2
 */