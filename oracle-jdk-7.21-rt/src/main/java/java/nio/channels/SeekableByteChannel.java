package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface SeekableByteChannel extends ByteChannel
{
  public abstract int read(ByteBuffer paramByteBuffer)
    throws IOException;

  public abstract int write(ByteBuffer paramByteBuffer)
    throws IOException;

  public abstract long position()
    throws IOException;

  public abstract SeekableByteChannel position(long paramLong)
    throws IOException;

  public abstract long size()
    throws IOException;

  public abstract SeekableByteChannel truncate(long paramLong)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.channels.SeekableByteChannel
 * JD-Core Version:    0.6.2
 */