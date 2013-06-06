package sun.plugin2.ipc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

public abstract class NamedPipe
  implements ReadableByteChannel, WritableByteChannel
{
  public abstract int read(ByteBuffer paramByteBuffer)
    throws IOException;

  public abstract int write(ByteBuffer paramByteBuffer)
    throws IOException;

  public abstract void close()
    throws IOException;

  public abstract boolean isOpen();

  public abstract String toString();

  public abstract Map getChildProcessParameters();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.NamedPipe
 * JD-Core Version:    0.6.2
 */