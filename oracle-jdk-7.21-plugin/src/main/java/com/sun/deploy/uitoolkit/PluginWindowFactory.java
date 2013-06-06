package com.sun.deploy.uitoolkit;

import sun.plugin2.main.client.ModalityInterface;
import sun.plugin2.message.Pipe;

public abstract class PluginWindowFactory extends WindowFactory
{
  public abstract Window createWindow(long paramLong, String paramString, boolean paramBoolean, ModalityInterface paramModalityInterface, Pipe paramPipe, int paramInt);

  public Window createWindow(long paramLong1, long paramLong2, boolean paramBoolean, ModalityInterface paramModalityInterface, Pipe paramPipe, int paramInt)
  {
    throw new RuntimeException("this should never been called");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.PluginWindowFactory
 * JD-Core Version:    0.6.2
 */