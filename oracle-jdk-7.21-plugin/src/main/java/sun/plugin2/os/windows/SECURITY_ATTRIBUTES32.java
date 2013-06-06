package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.StructAccessor;

class SECURITY_ATTRIBUTES32 extends SECURITY_ATTRIBUTES
{
  public static int size()
  {
    return 12;
  }

  SECURITY_ATTRIBUTES32(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }

  public SECURITY_ATTRIBUTES nLength(int paramInt)
  {
    this.accessor.setIntAt(0, paramInt);
    return this;
  }

  public int nLength()
  {
    return this.accessor.getIntAt(0);
  }

  public SECURITY_ATTRIBUTES bInheritHandle(int paramInt)
  {
    this.accessor.setIntAt(2, paramInt);
    return this;
  }

  public int bInheritHandle()
  {
    return this.accessor.getIntAt(2);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.SECURITY_ATTRIBUTES32
 * JD-Core Version:    0.6.2
 */