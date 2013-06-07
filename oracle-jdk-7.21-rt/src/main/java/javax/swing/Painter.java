package javax.swing;

import java.awt.Graphics2D;

public abstract interface Painter<T>
{
  public abstract void paint(Graphics2D paramGraphics2D, T paramT, int paramInt1, int paramInt2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.Painter
 * JD-Core Version:    0.6.2
 */