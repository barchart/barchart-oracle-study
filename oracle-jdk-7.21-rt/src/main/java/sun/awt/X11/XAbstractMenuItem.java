package sun.awt.X11;

import java.awt.Graphics;

public abstract interface XAbstractMenuItem
{
  public abstract int getWidth(Graphics paramGraphics);

  public abstract int getShortcutWidth(Graphics paramGraphics);

  public abstract String getLabel();

  public abstract int getHeight(Graphics paramGraphics);

  public abstract void paint(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public abstract void setMenuPeer(XMenuPeer paramXMenuPeer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XAbstractMenuItem
 * JD-Core Version:    0.6.2
 */