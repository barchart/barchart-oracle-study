package javax.swing.event;

import java.util.EventListener;

public abstract interface TreeExpansionListener extends EventListener
{
  public abstract void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent);

  public abstract void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.TreeExpansionListener
 * JD-Core Version:    0.6.2
 */