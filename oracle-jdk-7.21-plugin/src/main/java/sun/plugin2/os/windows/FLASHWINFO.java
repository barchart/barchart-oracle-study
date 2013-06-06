package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.BufferFactory;
import sun.plugin2.gluegen.runtime.CPU;
import sun.plugin2.gluegen.runtime.StructAccessor;

public abstract class FLASHWINFO
{
  StructAccessor accessor;

  public static int size()
  {
    if (CPU.is32Bit())
      return FLASHWINFO32.size();
    return FLASHWINFO64.size();
  }

  public static FLASHWINFO create()
  {
    return create(BufferFactory.newDirectByteBuffer(size()));
  }

  public static FLASHWINFO create(ByteBuffer paramByteBuffer)
  {
    if (CPU.is32Bit())
      return new FLASHWINFO32(paramByteBuffer);
    return new FLASHWINFO64(paramByteBuffer);
  }

  FLASHWINFO(ByteBuffer paramByteBuffer)
  {
    this.accessor = new StructAccessor(paramByteBuffer);
  }

  public ByteBuffer getBuffer()
  {
    return this.accessor.getBuffer();
  }

  public abstract FLASHWINFO cbSize(int paramInt);

  public abstract int cbSize();

  public abstract FLASHWINFO hwnd(long paramLong);

  public abstract long hwnd();

  public abstract FLASHWINFO dwFlags(int paramInt);

  public abstract int dwFlags();

  public abstract FLASHWINFO uCount(int paramInt);

  public abstract int uCount();

  public abstract FLASHWINFO dwTimeout(int paramInt);

  public abstract int dwTimeout();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.FLASHWINFO
 * JD-Core Version:    0.6.2
 */