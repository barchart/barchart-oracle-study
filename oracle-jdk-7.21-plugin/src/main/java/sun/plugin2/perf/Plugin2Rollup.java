package sun.plugin2.perf;

import com.sun.deploy.perf.PerfLabel;
import com.sun.deploy.perf.PerfRollup;
import java.io.PrintStream;

public class Plugin2Rollup
  implements PerfRollup
{
  long appletLaunchCosts;
  long jvmLaunchCosts;

  public Plugin2Rollup(long paramLong1, long paramLong2)
  {
    this.appletLaunchCosts = (paramLong1 / 1000L);
    this.jvmLaunchCosts = (paramLong2 / 1000L);
  }

  public void doRollup(PerfLabel[] paramArrayOfPerfLabel, PrintStream paramPrintStream)
  {
    if ((paramArrayOfPerfLabel != null) && (paramArrayOfPerfLabel.length > 0))
    {
      long l1 = paramArrayOfPerfLabel[0].getTime() / 1000L;
      long l2 = paramArrayOfPerfLabel[0].getTime1() / 1000L;
      boolean bool = true;
      if ((this.appletLaunchCosts < 0L) || (this.jvmLaunchCosts < 0L))
      {
        this.appletLaunchCosts = l1;
        this.jvmLaunchCosts = l2;
        bool = false;
      }
      long l3 = paramArrayOfPerfLabel[(paramArrayOfPerfLabel.length - 1)].getTime() / 1000L;
      long l4 = paramArrayOfPerfLabel[(paramArrayOfPerfLabel.length - 1)].getTime1() / 1000L;
      long l5 = l3 - l1;
      paramPrintStream.println();
      paramPrintStream.println("Plugin: Applet startup time................... ");
      paramPrintStream.println("     Total  time since applet start .............. " + pad(l3, 6, false) + " ms");
      paramPrintStream.println("     Total  time since JVM start .............. " + pad(l4, 6, false) + " ms");
      paramPrintStream.println("     Plugin time spend ........................ " + pad(l5, 6, false) + " ms");
      paramPrintStream.println("     Extern time spend since applet start ....... " + pad(l1, 6, false) + " ms");
      paramPrintStream.println("Plugin: Plug-in startup time.................. ");
      paramPrintStream.println("     Total time starting JVM................... " + pad(this.jvmLaunchCosts, 6, false) + " ms");
      paramPrintStream.println("     Total time starting Plugin................ " + pad(this.appletLaunchCosts, 6, false) + " ms");
      paramPrintStream.println("     Values from PluginMain ................... " + bool);
    }
  }

  public static String pad(long paramLong, int paramInt, boolean paramBoolean)
  {
    String str = Long.toString(paramLong);
    int i = paramInt - str.length();
    StringBuffer localStringBuffer = new StringBuffer(paramInt);
    int j;
    if (!paramBoolean)
      for (j = 0; j < i; j++)
        localStringBuffer.append(' ');
    localStringBuffer.append(str);
    if (paramBoolean == true)
      for (j = 0; j < i; j++)
        localStringBuffer.append(' ');
    return localStringBuffer.toString();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.perf.Plugin2Rollup
 * JD-Core Version:    0.6.2
 */