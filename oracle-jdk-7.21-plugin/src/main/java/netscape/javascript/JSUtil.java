package netscape.javascript;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class JSUtil
{
  public static String getStackTrace(Throwable paramThrowable)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter localPrintWriter = new PrintWriter(localByteArrayOutputStream);
    paramThrowable.printStackTrace(localPrintWriter);
    localPrintWriter.flush();
    return localByteArrayOutputStream.toString();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.javascript.JSUtil
 * JD-Core Version:    0.6.2
 */