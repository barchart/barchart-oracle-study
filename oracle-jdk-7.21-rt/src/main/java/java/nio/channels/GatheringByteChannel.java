package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface GatheringByteChannel extends WritableByteChannel
{
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;

  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.channels.GatheringByteChannel
 * JD-Core Version:    0.6.2
 */