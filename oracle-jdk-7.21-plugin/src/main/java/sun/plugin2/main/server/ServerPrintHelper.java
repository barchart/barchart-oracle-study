package sun.plugin2.main.server;

import java.nio.ByteBuffer;

public class ServerPrintHelper
{
  public static boolean isPrinterDC(long paramLong)
  {
    return isPrinterDC0(paramLong);
  }

  private static native boolean isPrinterDC0(long paramLong);

  public static boolean printBand(long paramLong, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
  {
    return printBand0(paramLong, paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramInt9);
  }

  private static native boolean printBand0(long paramLong, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.ServerPrintHelper
 * JD-Core Version:    0.6.2
 */