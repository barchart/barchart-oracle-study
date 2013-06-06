package com.sun.deploy.uitoolkit;

public abstract interface DragListener
{
  public abstract void appletDraggingToDesktop(DragContext paramDragContext);

  public abstract void appletDroppedOntoDesktop(DragContext paramDragContext);

  public abstract void appletExternalWindowClosed(DragContext paramDragContext);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.DragListener
 * JD-Core Version:    0.6.2
 */