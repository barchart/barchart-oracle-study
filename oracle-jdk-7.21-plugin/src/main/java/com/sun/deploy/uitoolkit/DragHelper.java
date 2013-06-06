package com.sun.deploy.uitoolkit;

public abstract interface DragHelper
{
  public abstract void register(DragContext paramDragContext, DragListener paramDragListener);

  public abstract void makeDisconnected(DragContext paramDragContext, Window paramWindow);

  public abstract void restore(DragContext paramDragContext);

  public abstract void unregister(DragContext paramDragContext);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.DragHelper
 * JD-Core Version:    0.6.2
 */