package sun.plugin2.jvm;

import java.util.EventListener;

public abstract interface JVMEventListener extends EventListener
{
  public abstract void jvmExited(ProcessLauncher paramProcessLauncher);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.jvm.JVMEventListener
 * JD-Core Version:    0.6.2
 */