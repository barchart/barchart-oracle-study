package javax.accessibility;

import java.awt.datatransfer.DataFlavor;
import java.io.InputStream;

public abstract interface AccessibleStreamable
{
  public abstract DataFlavor[] getMimeTypes();

  public abstract InputStream getStream(DataFlavor paramDataFlavor);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.accessibility.AccessibleStreamable
 * JD-Core Version:    0.6.2
 */