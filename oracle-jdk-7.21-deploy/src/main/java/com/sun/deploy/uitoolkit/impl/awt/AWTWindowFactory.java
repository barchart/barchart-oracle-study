package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.uitoolkit.Window;
import com.sun.deploy.uitoolkit.WindowFactory;

public class AWTWindowFactory extends WindowFactory
{
  public Window createWindow()
  {
    return new AWTFrameWindow();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTWindowFactory
 * JD-Core Version:    0.6.2
 */