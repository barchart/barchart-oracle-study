package java.lang;

import java.io.IOException;
import java.nio.CharBuffer;

public abstract interface Readable
{
  public abstract int read(CharBuffer paramCharBuffer)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Readable
 * JD-Core Version:    0.6.2
 */