package sun.plugin.navig.motif;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

public class OJIPlugin
{
  public static final int SpontFD = 10;
  private static Hashtable threads = new Hashtable();
  private static DataOutputStream spontOut;
  private static DataInputStream spontIn;

  public static void initialize()
  {
    spontIn = Plugin.newInput("Spont Comm", 10);
    spontOut = Plugin.newOutput("Spont Comm", 10);
    Object localObject1 = new Object();
    Object localObject2 = new Object();
    initializePipe(10, localObject1, localObject2);
  }

  public static void initializePipe(int paramInt, Object paramObject1, Object paramObject2)
  {
    nativeInitializePipe(paramInt, paramObject1, paramObject2);
  }

  public static int acquirePipeForCurrentThread()
  {
    AThread localAThread = getCurrentAThread();
    int i;
    if (localAThread != null)
    {
      i = localAThread.getPipe();
      Plugin.trace("OJIPlugin acq thread=:" + localAThread.toString() + " pipe=" + i);
    }
    else
    {
      i = 10;
      Plugin.trace("OJIPlugin acq Spontaneous pipe=" + i);
    }
    return i;
  }

  private static synchronized AThread getCurrentAThread()
  {
    Thread localThread = Thread.currentThread();
    Plugin.trace("Current thread:" + localThread.toString());
    if (threads.contains(localThread))
    {
      Plugin.trace("OJIPlugin: Retrieve the AThread\n");
      return (AThread)threads.get(localThread);
    }
    Plugin.trace("OJIPlugin: No AThread\n");
    return null;
  }

  public static synchronized void registerThread(AThread paramAThread)
  {
    Thread localThread = Thread.currentThread();
    Plugin.trace("Registering thread: " + localThread.toString() + " with AThread " + paramAThread.toString());
    threads.put(localThread, paramAThread);
  }

  private static native void nativeInitializePipe(int paramInt, Object paramObject1, Object paramObject2);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.navig.motif.OJIPlugin
 * JD-Core Version:    0.6.2
 */