package com.oracle.deploy.update;

import com.sun.deploy.config.Platform;
import java.awt.Toolkit;
import java.io.PrintStream;

public class Updater
{
  public static void main(String[] paramArrayOfString)
  {
    boolean bool = true;
    if (paramArrayOfString.length > 1)
      usage();
    if (paramArrayOfString.length == 1)
      if (paramArrayOfString[0].equals("-quiet"))
        bool = false;
      else
        usage();
    Toolkit.getDefaultToolkit();
    nativeStartUpdate(bool, true);
    label45: break label45;
  }

  public static void startUpdate(boolean paramBoolean)
  {
    nativeStartUpdate(paramBoolean, false);
  }

  private static void usage()
  {
    System.err.println("usage: java " + Updater.class.getName() + " [-quiet]");
    System.err.println("   -quiet: Download and install latest version without prompt.");
    System.err.println("   <none>: Check for update and prompt user if available.");
    System.exit(1);
  }

  private static native void nativeStartUpdate(boolean paramBoolean1, boolean paramBoolean2);

  static
  {
    Platform.get().loadDeployNativeLib();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.oracle.deploy.update.Updater
 * JD-Core Version:    0.6.2
 */