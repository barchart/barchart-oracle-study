package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.StructAccessor;

class FLASHWINFO64 extends FLASHWINFO
{
  public static int size()
  {
    return 28;
  }

  FLASHWINFO64(ByteBuffer paramByteBuffer)
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
    this.accessor.setLongAt(1, paramLong);
    return this;
  }

  public long hwnd()
  {
    return this.accessor.getLongAt(1);
  }

  public FLASHWINFO dwFlags(int paramInt)
  {
    this.accessor.setIntAt(4, paramInt);
    return this;
  }

  public int dwFlags()
  {
    return this.accessor.getIntAt(4);
  }

  public FLASHWINFO uCount(int paramInt)
  {
    this.accessor.setIntAt(5, paramInt);
    return this;
  }

  public int uCount()
  {
    return this.accessor.getIntAt(5);
  }

  public FLASHWINFO dwTimeout(int paramInt)
  {
    this.accessor.setIntAt(6, paramInt);
    return this;
  }

  public int dwTimeout()
  {
    return this.accessor.getIntAt(6);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.FLASHWINFO64
 * JD-Core Version:    0.6.2
 */