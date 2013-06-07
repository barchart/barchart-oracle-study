package javax.swing;

import java.awt.Component;
import java.awt.Graphics;

public abstract interface Icon
{
  public abstract void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2);

  public abstract int getIconWidth();

  public abstract int getIconHeight();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.Icon
 * JD-Core Version:    0.6.2
 */