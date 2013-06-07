package java.awt.peer;

import java.awt.Point;
import java.awt.Window;

public abstract interface MouseInfoPeer
{
  public abstract int fillPointWithCoords(Point paramPoint);

  public abstract boolean isWindowUnderMouse(Window paramWindow);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.MouseInfoPeer
 * JD-Core Version:    0.6.2
 */