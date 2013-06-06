package com.sun.deploy.uitoolkit.impl.text;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.PluginWindowFactory;
import com.sun.deploy.uitoolkit.Window;
import com.sun.deploy.uitoolkit.WindowFactory;
import sun.plugin2.main.client.ModalityInterface;
import sun.plugin2.message.Pipe;

public class TextPluginWindowFactory extends PluginWindowFactory
{
  private WindowFactory basicFactory = new TextWindowFactory();

  public Window createWindow()
  {
    return this.basicFactory.createWindow();
  }

  public Window createWindow(long paramLong, String paramString, boolean paramBoolean, ModalityInterface paramModalityInterface, Pipe paramPipe, int paramInt)
  {
    Trace.println("createWindow(" + paramLong + ", " + paramString + ", " + paramBoolean + ", " + paramModalityInterface + ", " + paramPipe + ", " + paramInt + ")");
    return createWindow();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.text.TextPluginWindowFactory
 * JD-Core Version:    0.6.2
 */