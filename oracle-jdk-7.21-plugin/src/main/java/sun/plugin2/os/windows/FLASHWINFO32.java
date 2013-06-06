package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.StructAccessor;

class FLASHWINFO32 extends FLASHWINFO
{
  public static int size()
  {
    return 20;
  }

  FLASHWINFO32(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }

  public FLASHWINFO cbSize(int paramInt)
  {
    this.accessor.setIntAt(0, paramInt);
    return this;
  }

  public int cbSize()
  {
    return this.accessor.getIntAt(0);
  }

  public FLASHWINFO hwnd(long paramLong)
  {
    this.accessor.setIntAt(1, (int)paramLong);
    return this;
  }

  public long hwnd()
  {
    return this.accessor.getIntAt(1);
  }

  public FLASHWINFO dwFlags(int paramInt)
  {
    this.accessor.setIntAt(2, paramInt);
    return this;
  }

  public int dwFlags()
  {
    return this.accessor.getIntAt(2);
  }

  public FLASHWINFO uCount(int paramInt)
  {
    this.accessor.setIntAt(3, paramInt);
    return this;
  }

  public int uCount()
  {
    return this.accessor.getIntAt(3);
  }

  public FLASHWINFO dwTimeout(int paramInt)
  {
    this.accessor.setIntAt(4, paramInt);
    return this;
  }

  public int dwTimeout()
  {
    return this.accessor.getIntAt(4);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.FLASHWINFO32
 * JD-Core Version:    0.6.2
 */