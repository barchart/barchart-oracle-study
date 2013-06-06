package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.appcontext.AppContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.io.PrintStream;
import sun.plugin2.main.client.ModalityInterface;
import sun.plugin2.main.client.PluginEmbeddedFrame;
import sun.plugin2.message.Pipe;

public class AWTPluginEmbeddedFrameWindow extends AWTFrameWindow
{
  AppContext appContext;

  AWTPluginEmbeddedFrameWindow(long paramLong, String paramString, boolean paramBoolean, ModalityInterface paramModalityInterface, Pipe paramPipe, int paramInt)
  {
    PluginEmbeddedFrame localPluginEmbeddedFrame = new PluginEmbeddedFrame(paramLong, paramString, paramBoolean, paramModalityInterface, paramPipe, paramInt);
    localPluginEmbeddedFrame.setLayout(new BorderLayout());
    setFrame(localPluginEmbeddedFrame);
  }

  public int getWindowLayerID()
  {
    PluginEmbeddedFrame localPluginEmbeddedFrame = (PluginEmbeddedFrame)getFrame();
    return localPluginEmbeddedFrame.getLayerID();
  }

  public void setAppContext(AppContext paramAppContext)
  {
    this.appContext = paramAppContext;
  }

  public void invokeLater(Runnable paramRunnable)
  {
    if (this.appContext == null)
      System.out.println("NO APPCONTEXT");
    else
      this.appContext.invokeLater(paramRunnable);
  }

  public void invokeLater(Component paramComponent, Runnable paramRunnable)
  {
    OldPluginAWTUtil.invokeLater(paramComponent, paramRunnable);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTPluginEmbeddedFrameWindow
 * JD-Core Version:    0.6.2
 */