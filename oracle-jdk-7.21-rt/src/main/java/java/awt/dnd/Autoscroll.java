package java.awt.dnd;

import java.awt.Insets;
import java.awt.Point;

public abstract interface Autoscroll
{
  public abstract Insets getAutoscrollInsets();

  public abstract void autoscroll(Point paramPoint);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.dnd.Autoscroll
 * JD-Core Version:    0.6.2
 */