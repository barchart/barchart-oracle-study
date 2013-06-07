package sun.security.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract interface DerEncoder
{
  public abstract void derEncode(OutputStream paramOutputStream)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.util.DerEncoder
 * JD-Core Version:    0.6.2
 */