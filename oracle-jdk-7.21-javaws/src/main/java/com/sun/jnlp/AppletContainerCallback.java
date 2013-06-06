package com.sun.jnlp;

import java.awt.Dimension;
import java.net.URL;

public abstract interface AppletContainerCallback
{
  public abstract void showDocument(URL paramURL);

  public abstract void relativeResize(Dimension paramDimension);
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.jnlp.AppletContainerCallback
 * JD-Core Version:    0.6.2
 */