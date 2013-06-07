package javax.swing.event;

import java.util.EventListener;
import javax.swing.tree.ExpandVetoException;

public abstract interface TreeWillExpandListener extends EventListener
{
  public abstract void treeWillExpand(TreeExpansionEvent paramTreeExpansionEvent)
    throws ExpandVetoException;

  public abstract void treeWillCollapse(TreeExpansionEvent paramTreeExpansionEvent)
    throws ExpandVetoException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.TreeWillExpandListener
 * JD-Core Version:    0.6.2
 */