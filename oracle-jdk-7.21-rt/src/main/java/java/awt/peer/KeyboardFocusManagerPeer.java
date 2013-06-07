package java.awt.peer;

import java.awt.Component;
import java.awt.Window;

public abstract interface KeyboardFocusManagerPeer
{
  public abstract Window getCurrentFocusedWindow();

  public abstract void setCurrentFocusOwner(Component paramComponent);

  public abstract Component getCurrentFocusOwner();

  public abstract void clearGlobalFocusOwner(Window paramWindow);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.KeyboardFocusManagerPeer
 * JD-Core Version:    0.6.2
 */