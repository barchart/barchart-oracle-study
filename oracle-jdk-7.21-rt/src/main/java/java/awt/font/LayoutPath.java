package java.awt.font;

import java.awt.geom.Point2D;

public abstract class LayoutPath
{
  public abstract boolean pointToPath(Point2D paramPoint2D1, Point2D paramPoint2D2);

  public abstract void pathToPoint(Point2D paramPoint2D1, boolean paramBoolean, Point2D paramPoint2D2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.font.LayoutPath
 * JD-Core Version:    0.6.2
 */