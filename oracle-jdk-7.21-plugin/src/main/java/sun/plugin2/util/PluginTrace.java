package sun.plugin2.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PluginTrace
{
  static boolean enabled = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;

  public static void init()
  {
  }

  private static void redirectStdout()
  {
    if (!enabled)
      return;
    ByteArrayOutputStream local1 = new ByteArrayOutputStream()
    {
      public synchronized void flush()
      {
        String str = toString();
        if (!str.endsWith("\n"))
          return;
        PluginTrace.broadcast(str);
        reset();
      }
    };
    System.setOut(new PrintStream(local1, true));
  }

  private static native void broadcast(String paramString);

  static
  {
    redirectStdout();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.util.PluginTrace
 * JD-Core Version:    0.6.2
 */