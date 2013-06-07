package javax.swing.event;

import java.util.EventListener;

public abstract interface UndoableEditListener extends EventListener
{
  public abstract void undoableEditHappened(UndoableEditEvent paramUndoableEditEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.UndoableEditListener
 * JD-Core Version:    0.6.2
 */