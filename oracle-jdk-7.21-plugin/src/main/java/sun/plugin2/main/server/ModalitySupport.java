package sun.plugin2.main.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ModalitySupport
{
  private static Map pluginInfoMap = new HashMap();

  public static synchronized void initialize(int paramInt, Plugin paramPlugin)
  {
    PerPluginInfo localPerPluginInfo = (PerPluginInfo)pluginInfoMap.get(new Integer(paramInt));
    if (localPerPluginInfo == null)
      pluginInfoMap.put(new Integer(paramInt), new PerPluginInfo(paramPlugin));
    else
      assert (localPerPluginInfo.getPlugin() == paramPlugin);
  }

  public static synchronized void shutdown(int paramInt)
  {
    PerPluginInfo localPerPluginInfo = (PerPluginInfo)pluginInfoMap.remove(new Integer(paramInt));
    localPerPluginInfo.getPlugin().notifyMainThread();
  }

  public static void modalityChanged(int paramInt, boolean paramBoolean)
  {
    PerPluginInfo localPerPluginInfo = (PerPluginInfo)pluginInfoMap.get(new Integer(paramInt));
    if (localPerPluginInfo == null)
      return;
    final AppletID localAppletID = new AppletID(paramInt);
    if (paramBoolean)
    {
      final int i = localPerPluginInfo.modalityPushed();
      final Plugin localPlugin = localPerPluginInfo.getPlugin();
      localPlugin.invokeLater(new Runnable()
      {
        private final ModalitySupport.PerPluginInfo val$info;
        private final AppletID val$id;
        private final int val$depth;
        private final Plugin val$plugin;

        public void run()
        {
          if (this.val$info.getPlugin().getActiveJSCounter() > 0)
          {
            this.val$info.modalityPopped();
            return;
          }
          while ((!JVMManager.getManager().appletExited(localAppletID)) && (this.val$info.getModalityDepth() >= i))
            localPlugin.waitForSignalWithModalBlocking();
          localPlugin.notifyMainThread();
        }
      });
    }
    else
    {
      localPerPluginInfo.modalityPopped();
    }
  }

  public static synchronized boolean appletShouldBlockBrowser(int paramInt)
  {
    Thread localThread = Thread.currentThread();
    PerPluginInfo localPerPluginInfo = (PerPluginInfo)pluginInfoMap.get(new Integer(paramInt));
    if ((localPerPluginInfo != null) && (localPerPluginInfo.getModalityDepth() > 0))
      return true;
    Iterator localIterator = pluginInfoMap.values().iterator();
    while (localIterator.hasNext())
    {
      localPerPluginInfo = (PerPluginInfo)localIterator.next();
      if ((localPerPluginInfo.getPluginMainThread() == localThread) && (localPerPluginInfo.getModalityDepth() > 0))
        return true;
    }
    return false;
  }

  public static boolean appletShouldBlockBrowser(AppletID paramAppletID)
  {
    if (paramAppletID == null)
      return false;
    return appletShouldBlockBrowser(paramAppletID.getID());
  }

  public static synchronized Integer getAppletBlockingBrowser()
  {
    Thread localThread = Thread.currentThread();
    Iterator localIterator = pluginInfoMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      PerPluginInfo localPerPluginInfo = (PerPluginInfo)pluginInfoMap.get(localInteger);
      if ((localPerPluginInfo.getPluginMainThread() == localThread) && (localPerPluginInfo.getModalityDepth() > 0))
        return localInteger;
    }
    return null;
  }

  static class PerPluginInfo
  {
    private Plugin plugin;
    private Thread pluginMainThread;
    private int modalityDepth;

    public PerPluginInfo(Plugin paramPlugin)
    {
      this.plugin = paramPlugin;
      this.pluginMainThread = Thread.currentThread();
    }

    public Plugin getPlugin()
    {
      return this.plugin;
    }

    public Thread getPluginMainThread()
    {
      return this.pluginMainThread;
    }

    public synchronized int modalityPushed()
    {
      return ++this.modalityDepth;
    }

    public synchronized void modalityPopped()
    {
      this.modalityDepth -= 1;
      this.plugin.notifyMainThread();
    }

    public synchronized int getModalityDepth()
    {
      return this.modalityDepth;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.ModalitySupport
 * JD-Core Version:    0.6.2
 */