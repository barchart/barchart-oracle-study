package java.awt.peer;

import java.awt.MenuBar;
import java.awt.Rectangle;

public abstract interface FramePeer extends WindowPeer
{
  public abstract void setTitle(String paramString);

  public abstract void setMenuBar(MenuBar paramMenuBar);

  public abstract void setResizable(boolean paramBoolean);

  public abstract void setState(int paramInt);

  public abstract int getState();

  public abstract void setMaximizedBounds(Rectangle paramRectangle);

  public abstract void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract Rectangle getBoundsPrivate();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.FramePeer
 * JD-Core Version:    0.6.2
 */