package java.awt.event;

import java.util.EventListener;

public abstract interface MouseListener extends EventListener
{
  public abstract void mouseClicked(MouseEvent paramMouseEvent);

  public abstract void mousePressed(MouseEvent paramMouseEvent);

  public abstract void mouseReleased(MouseEvent paramMouseEvent);

  public abstract void mouseEntered(MouseEvent paramMouseEvent);

  public abstract void mouseExited(MouseEvent paramMouseEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.MouseListener
 * JD-Core Version:    0.6.2
 */