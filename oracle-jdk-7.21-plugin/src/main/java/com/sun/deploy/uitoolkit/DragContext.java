package com.sun.deploy.uitoolkit;

public abstract interface DragContext
{
  public abstract Integer getAppletId();

  public abstract Applet2Adapter getApplet2Adapter();

  public abstract int getModalityLevel();

  public abstract Window getParentContainer();

  public abstract String getDraggedTitle();

  public abstract boolean getUndecorated();

  public abstract boolean isDisconnected();

  public abstract boolean isSignedApplet();

  public abstract void setDraggedApplet();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.DragContext
 * JD-Core Version:    0.6.2
 */