package com.sun.deploy.ui;

import java.awt.Component;

public abstract interface DeployEmbeddedFrameIf
{
  public abstract void push(Component paramComponent);

  public abstract Component pop();
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.ui.DeployEmbeddedFrameIf
 * JD-Core Version:    0.6.2
 */