package sun.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ByteBuffered
{
  public abstract ByteBuffer getByteBuffer()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ByteBuffered
 * JD-Core Version:    0.6.2
 */