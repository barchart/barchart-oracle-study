package sun.plugin2.os.windows;

import java.nio.ByteBuffer;

class OVERLAPPED64 extends OVERLAPPED
{
  public static int size()
  {
    return 0;
  }

  OVERLAPPED64(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.OVERLAPPED64
 * JD-Core Version:    0.6.2
 */