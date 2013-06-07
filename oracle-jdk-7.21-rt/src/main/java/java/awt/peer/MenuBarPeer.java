package java.awt.peer;

import java.awt.Menu;

public abstract interface MenuBarPeer extends MenuComponentPeer
{
  public abstract void addMenu(Menu paramMenu);

  public abstract void delMenu(int paramInt);

  public abstract void addHelpMenu(Menu paramMenu);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.MenuBarPeer
 * JD-Core Version:    0.6.2
 */