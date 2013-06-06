package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.uitoolkit.PluginWindowFactory;
import com.sun.deploy.uitoolkit.Window;
import com.sun.deploy.uitoolkit.WindowFactory;
import sun.plugin2.main.client.ModalityInterface;
import sun.plugin2.message.Pipe;

public class AWTPluginWindowFactory extends PluginWindowFactory
{
  private WindowFactory basicFactory = new AWTWindowFactory();

  public Window createWindow()
  {
    return this.basicFactory.createWindow();
  }

  public Window createWindow(long paramLong, String paramString, boolean paramBoolean, ModalityInterface paramModalityInterface, Pipe paramPipe, int paramInt)
  {
    return new AWTPluginEmbeddedFrameWindow(paramLong, paramString, paramBoolean, paramModalityInterface, paramPipe, paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTPluginWindowFactory
 * JD-Core Version:    0.6.2
 */