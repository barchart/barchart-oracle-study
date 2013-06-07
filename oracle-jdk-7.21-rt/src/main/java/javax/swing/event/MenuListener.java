package javax.swing.event;

import java.util.EventListener;

public abstract interface MenuListener extends EventListener
{
  public abstract void menuSelected(MenuEvent paramMenuEvent);

  public abstract void menuDeselected(MenuEvent paramMenuEvent);

  public abstract void menuCanceled(MenuEvent paramMenuEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.MenuListener
 * JD-Core Version:    0.6.2
 */