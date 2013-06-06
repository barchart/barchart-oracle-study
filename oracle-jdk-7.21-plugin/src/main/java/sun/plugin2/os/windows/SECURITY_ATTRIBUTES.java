package sun.plugin2.os.windows;

import java.nio.ByteBuffer;
import sun.plugin2.gluegen.runtime.BufferFactory;
import sun.plugin2.gluegen.runtime.CPU;
import sun.plugin2.gluegen.runtime.StructAccessor;

public abstract class SECURITY_ATTRIBUTES
{
  StructAccessor accessor;

  public static int size()
  {
    if (CPU.is32Bit())
      return SECURITY_ATTRIBUTES32.size();
    return SECURITY_ATTRIBUTES64.size();
  }

  public static SECURITY_ATTRIBUTES create()
  {
    return create(BufferFactory.newDirectByteBuffer(size()));
  }

  public static SECURITY_ATTRIBUTES create(ByteBuffer paramByteBuffer)
  {
    if (CPU.is32Bit())
      return new SECURITY_ATTRIBUTES32(paramByteBuffer);
    return new SECURITY_ATTRIBUTES64(paramByteBuffer);
  }

  SECURITY_ATTRIBUTES(ByteBuffer paramByteBuffer)
  {
    this.accessor = new StructAccessor(paramByteBuffer);
  }

  public ByteBuffer getBuffer()
  {
    return this.accessor.getBuffer();
  }

  public abstract SECURITY_ATTRIBUTES nLength(int paramInt);

  public abstract int nLength();

  public abstract SECURITY_ATTRIBUTES bInheritHandle(int paramInt);

  public abstract int bInheritHandle();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.SECURITY_ATTRIBUTES
 * JD-Core Version:    0.6.2
 */