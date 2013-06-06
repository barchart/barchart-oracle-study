package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.BufferFactory;
import sun.plugin2.gluegen.runtime.CPU;
import sun.plugin2.gluegen.runtime.StructAccessor;

public abstract class OVERLAPPED
{
  StructAccessor accessor;

  public static int size()
  {
    if (CPU.is32Bit())
      return OVERLAPPED32.size();
    return OVERLAPPED64.size();
  }

  public static OVERLAPPED create()
  {
    return create(BufferFactory.newDirectByteBuffer(size()));
  }

  public static OVERLAPPED create(ByteBuffer paramByteBuffer)
  {
    if (CPU.is32Bit())
      return new OVERLAPPED32(paramByteBuffer);
    return new OVERLAPPED64(paramByteBuffer);
  }

  OVERLAPPED(ByteBuffer paramByteBuffer)
  {
    this.accessor = new StructAccessor(paramByteBuffer);
  }

  public ByteBuffer getBuffer()
  {
    return this.accessor.getBuffer();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.OVERLAPPED
 * JD-Core Version:    0.6.2
 */