package sun.awt;

import java.awt.Point;
import java.awt.Window;
import java.awt.peer.MouseInfoPeer;

public class DefaultMouseInfoPeer
  implements MouseInfoPeer
{
  public native int fillPointWithCoords(Point paramPoint);

  public native boolean isWindowUnderMouse(Window paramWindow);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.DefaultMouseInfoPeer
 * JD-Core Version:    0.6.2
 */