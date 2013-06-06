package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.BufferFactory;
import sun.plugin2.gluegen.runtime.CPU;
import sun.plugin2.gluegen.runtime.StructAccessor;

public abstract class OSVERSIONINFOA
{
  StructAccessor accessor;

  public static int size()
  {
    if (CPU.is32Bit())
      return OSVERSIONINFOA32.size();
    return OSVERSIONINFOA64.size();
  }

  public static OSVERSIONINFOA create()
  {
    return create(BufferFactory.newDirectByteBuffer(size()));
  }

  public static OSVERSIONINFOA create(ByteBuffer paramByteBuffer)
  {
    if (CPU.is32Bit())
      return new OSVERSIONINFOA32(paramByteBuffer);
    return new OSVERSIONINFOA64(paramByteBuffer);
  }

  OSVERSIONINFOA(ByteBuffer paramByteBuffer)
  {
    this.accessor = new StructAccessor(paramByteBuffer);
  }

  public ByteBuffer getBuffer()
  {
    return this.accessor.getBuffer();
  }

  public abstract OSVERSIONINFOA dwOSVersionInfoSize(int paramInt);

  public abstract int dwOSVersionInfoSize();

  public abstract OSVERSIONINFOA dwMajorVersion(int paramInt);

  public abstract int dwMajorVersion();

  public abstract OSVERSIONINFOA dwMinorVersion(int paramInt);

  public abstract int dwMinorVersion();

  public abstract OSVERSIONINFOA dwBuildNumber(int paramInt);

  public abstract int dwBuildNumber();

  public abstract OSVERSIONINFOA dwPlatformId(int paramInt);

  public abstract int dwPlatformId();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.OSVERSIONINFOA
 * JD-Core Version:    0.6.2
 */