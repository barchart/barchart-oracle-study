package sun.plugin2.jvm;

import java.io.InputStream;

public abstract interface ProcessLauncher
{
  public abstract long getJVMLaunchTime();

  public abstract void addParameter(String paramString);

  public abstract void start();

  public abstract Exception getErrorDuringStartup();

  public abstract InputStream getInputStream();

  public abstract InputStream getErrorStream();

  public abstract boolean exited();

  public abstract int getExitCode();

  public abstract void addJVMEventListener(JVMEventListener paramJVMEventListener);

  public abstract void clearUserArguments();

  public abstract void destroy();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.jvm.ProcessLauncher
 * JD-Core Version:    0.6.2
 */