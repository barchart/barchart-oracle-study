package sun.plugin2.gluegen.runtime;

public class CPU
{
  private static boolean is32Bit;

  public static boolean is32Bit()
  {
    return is32Bit;
  }

  static
  {
    String str1 = System.getProperty("os.name").toLowerCase();
    String str2 = System.getProperty("os.arch").toLowerCase();
    if (((str1.startsWith("windows")) && (str2.equals("x86"))) || ((str1.startsWith("linux")) && (str2.equals("i386"))) || ((str1.startsWith("linux")) && (str2.equals("arm"))) || ((str1.startsWith("linux")) && (str2.equals("ppc"))) || ((str1.indexOf("os x") != -1) && (str2.equals("ppc"))) || ((str1.indexOf("os x") != -1) && (str2.equals("i386"))) || ((str1.startsWith("sunos")) && (str2.equals("sparc"))) || ((str1.startsWith("sunos")) && (str2.equals("x86"))) || ((str1.startsWith("freebsd")) && (str2.equals("i386"))) || ((str1.startsWith("hp-ux")) && (str2.equals("pa_risc2.0"))))
      is32Bit = true;
    else if (((!str1.startsWith("windows")) || (!str2.equals("amd64"))) && ((!str1.startsWith("linux")) || (!str2.equals("amd64"))) && ((!str1.startsWith("linux")) || (!str2.equals("x86_64"))) && ((!str1.startsWith("linux")) || (!str2.equals("ia64"))) && ((str1.indexOf("os x") == -1) || (!str2.equals("x86_64"))) && ((!str1.startsWith("sunos")) || (!str2.equals("sparcv9"))) && ((!str1.startsWith("sunos")) || (!str2.equals("amd64"))))
      throw new RuntimeException("Please port CPU detection (32/64 bit) to your platform (" + str1 + "/" + str2 + ")");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.gluegen.runtime.CPU
 * JD-Core Version:    0.6.2
 */