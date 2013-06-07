package java.awt.event;

import java.util.EventListener;

public abstract interface HierarchyBoundsListener extends EventListener
{
  public abstract void ancestorMoved(HierarchyEvent paramHierarchyEvent);

  public abstract void ancestorResized(HierarchyEvent paramHierarchyEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.HierarchyBoundsListener
 * JD-Core Version:    0.6.2
 */