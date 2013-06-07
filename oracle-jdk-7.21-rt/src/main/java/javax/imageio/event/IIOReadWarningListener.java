package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;

public abstract interface IIOReadWarningListener extends EventListener
{
  public abstract void warningOccurred(ImageReader paramImageReader, String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.imageio.event.IIOReadWarningListener
 * JD-Core Version:    0.6.2
 */