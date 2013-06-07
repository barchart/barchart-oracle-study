package java.awt.dnd;

import java.util.EventListener;

public abstract interface DropTargetListener extends EventListener
{
  public abstract void dragEnter(DropTargetDragEvent paramDropTargetDragEvent);

  public abstract void dragOver(DropTargetDragEvent paramDropTargetDragEvent);

  public abstract void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent);

  public abstract void dragExit(DropTargetEvent paramDropTargetEvent);

  public abstract void drop(DropTargetDropEvent paramDropTargetDropEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.dnd.DropTargetListener
 * JD-Core Version:    0.6.2
 */