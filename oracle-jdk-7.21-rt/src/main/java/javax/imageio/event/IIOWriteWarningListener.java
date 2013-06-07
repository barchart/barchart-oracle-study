package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageWriter;

public abstract interface IIOWriteWarningListener extends EventListener
{
  public abstract void warningOccurred(ImageWriter paramImageWriter, int paramInt, String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.imageio.event.IIOWriteWarningListener
 * JD-Core Version:    0.6.2
 */