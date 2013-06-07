package sun.awt;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import sun.java2d.SurfaceData;

public abstract interface X11ComponentPeer
{
  public abstract long getContentWindow();

  public abstract SurfaceData getSurfaceData();

  public abstract GraphicsConfiguration getGraphicsConfiguration();

  public abstract ColorModel getColorModel();

  public abstract Rectangle getBounds();

  public abstract Graphics getGraphics();

  public abstract Object getTarget();

  public abstract void setFullScreenExclusiveModeState(boolean paramBoolean);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11ComponentPeer
 * JD-Core Version:    0.6.2
 */