package sun.plugin2.main.server;

import java.io.IOException;
import sun.plugin2.util.SystemUtil;

public abstract class ResultHandler
{
  protected static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;

  public void waitForResult(ResultID paramResultID, AppletID paramAppletID)
    throws IOException
  {
    int i = JVMManager.getManager().getJVMIDForApplet(paramAppletID);
    for (int j = i; (!LiveConnectSupport.resultAvailable(paramResultID)) && (!JVMManager.getManager().instanceExited(j)) && (!JVMManager.getManager().appletExited(paramAppletID)) && (j >= 0); j = JVMManager.getManager().getJVMIDForApplet(paramAppletID))
      waitForSignal();
  }

  public void waitForResult(ResultID paramResultID, int paramInt, AppletID paramAppletID)
  {
    for (int i = JVMManager.getManager().getJVMIDForApplet(paramAppletID); (!LiveConnectSupport.resultAvailable(paramResultID)) && (!JVMManager.getManager().instanceExited(paramInt)) && (!JVMManager.getManager().appletExited(paramAppletID)) && (i >= 0) && (paramInt == i); i = JVMManager.getManager().getJVMIDForApplet(paramAppletID))
      waitForSignal();
  }

  public abstract void waitForSignal();

  public abstract void waitForSignal(long paramLong);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.ResultHandler
 * JD-Core Version:    0.6.2
 */