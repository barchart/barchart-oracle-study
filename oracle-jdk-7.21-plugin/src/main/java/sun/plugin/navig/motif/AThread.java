package sun.plugin.navig.motif;

class AThread extends Thread
{
  private int pipe = -1;
  private static boolean is_initialized;

  private AThread(int paramInt)
  {
    initIfFirstCall();
    OJIPlugin.initializePipe(paramInt, null, null);
    this.pipe = paramInt;
  }

  public void run()
  {
    OJIPlugin.registerThread(this);
    JNIHandleLoop();
  }

  public int getPipe()
  {
    return this.pipe;
  }

  void JNIHandleLoop()
  {
    handleRequest(this.pipe);
  }

  private static synchronized void initIfFirstCall()
  {
    if (is_initialized)
      return;
    initGlobals();
    is_initialized = true;
  }

  private native void handleRequest(int paramInt);

  private static native void initGlobals();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.navig.motif.AThread
 * JD-Core Version:    0.6.2
 */