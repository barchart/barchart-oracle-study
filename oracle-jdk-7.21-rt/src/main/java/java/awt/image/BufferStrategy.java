package java.awt.image;

import java.awt.BufferCapabilities;
import java.awt.Graphics;

public abstract class BufferStrategy
{
  public abstract BufferCapabilities getCapabilities();

  public abstract Graphics getDrawGraphics();

  public abstract boolean contentsLost();

  public abstract boolean contentsRestored();

  public abstract void show();

  public void dispose()
  {
  }
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.image.BufferStrategy
 * JD-Core Version:    0.6.2
 */