package javax.swing.event;

import java.util.EventListener;

public abstract interface MenuKeyListener extends EventListener
{
  public abstract void menuKeyTyped(MenuKeyEvent paramMenuKeyEvent);

  public abstract void menuKeyPressed(MenuKeyEvent paramMenuKeyEvent);

  public abstract void menuKeyReleased(MenuKeyEvent paramMenuKeyEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.MenuKeyListener
 * JD-Core Version:    0.6.2
 */