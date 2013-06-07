package javax.swing.event;

import java.util.EventListener;

public abstract interface PopupMenuListener extends EventListener
{
  public abstract void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent);

  public abstract void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent);

  public abstract void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.PopupMenuListener
 * JD-Core Version:    0.6.2
 */