package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.StructAccessor;

class OSVERSIONINFOA32 extends OSVERSIONINFOA
{
  public static int size()
  {
    return 148;
  }

  OSVERSIONINFOA32(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }

  public OSVERSIONINFOA dwOSVersionInfoSize(int paramInt)
  {
    this.accessor.setIntAt(0, paramInt);
    return this;
  }

  public int dwOSVersionInfoSize()
  {
    return this.accessor.getIntAt(0);
  }

  public OSVERSIONINFOA dwMajorVersion(int paramInt)
  {
    this.accessor.setIntAt(1, paramInt);
    return this;
  }

  public int dwMajorVersion()
  {
    return this.accessor.getIntAt(1);
  }

  public OSVERSIONINFOA dwMinorVersion(int paramInt)
  {
    this.accessor.setIntAt(2, paramInt);
    return this;
  }

  public int dwMinorVersion()
  {
    return this.accessor.getIntAt(2);
  }

  public OSVERSIONINFOA dwBuildNumber(int paramInt)
  {
    this.accessor.setIntAt(3, paramInt);
    return this;
  }

  public int dwBuildNumber()
  {
    return this.accessor.getIntAt(3);
  }

  public OSVERSIONINFOA dwPlatformId(int paramInt)
  {
    this.accessor.setIntAt(4, paramInt);
    return this;
  }

  public int dwPlatformId()
  {
    return this.accessor.getIntAt(4);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.OSVERSIONINFOA32
 * JD-Core Version:    0.6.2
 */