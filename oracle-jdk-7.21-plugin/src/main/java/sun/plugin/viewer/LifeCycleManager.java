package sun.plugin.viewer;

import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.trace.Trace;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import sun.plugin.AppletViewer;
import sun.plugin.services.BrowserService;

public class LifeCycleManager
{
  private static final Object browserListenerLock = new Object();
  private static boolean browserListenerInstalled = false;
  private static final LinkedHashMap appletPanels = new LinkedHashMap();

  public static AppletViewer getAppletPanel(String paramString)
  {
    AppletViewer localAppletViewer = null;
    synchronized (appletPanels)
    {
      localAppletViewer = (AppletViewer)appletPanels.remove(paramString);
    }
    if (localAppletViewer != null)
      Trace.msgPrintln("lifecycle.applet.found");
    return localAppletViewer;
  }

  public static void checkLifeCycle(AppletViewer paramAppletViewer)
  {
    if (paramAppletViewer.isLegacyLifeCycle())
      installBrowserEventListener();
  }

  private static void installBrowserEventListener()
  {
    if (browserListenerInstalled)
      return;
    synchronized (browserListenerLock)
    {
      if (!browserListenerInstalled)
      {
        BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
        browserListenerInstalled = localBrowserService.installBrowserEventListener();
      }
    }
  }

  private static void add(String paramString, AppletViewer paramAppletViewer)
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (appletPanels)
    {
      int i = appletPanels.size() - Integer.getInteger("javaplugin.lifecycle.cachesize", 1).intValue();
      if (i < 0)
        i = 0;
      Set localSet = appletPanels.keySet();
      Iterator localIterator = localSet.iterator();
      for (int j = 0; (j < i) && (localIterator.hasNext()); j++)
      {
        String str = (String)localIterator.next();
        localArrayList.add(appletPanels.remove(str));
      }
      appletPanels.put(paramString, paramAppletViewer);
    }
    if (localArrayList.size() > 0)
      Trace.msgPrintln("lifecycle.applet.cachefull");
    ??? = localArrayList.iterator();
    while (((Iterator)???).hasNext())
    {
      AppletViewer localAppletViewer = (AppletViewer)((Iterator)???).next();
      localAppletViewer.appletDestroy();
    }
    localArrayList.clear();
  }

  public static String getIdentifier(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfString1.length; i++)
      if (paramArrayOfString1[i] != null)
      {
        localStringBuffer.append("<NAME=");
        localStringBuffer.append(paramArrayOfString1[i]);
        localStringBuffer.append(" VALUE=");
        localStringBuffer.append(paramArrayOfString2[i]);
        localStringBuffer.append(">");
      }
    return localStringBuffer.toString();
  }

  public static void loadAppletPanel(AppletViewer paramAppletViewer)
  {
    if ((paramAppletViewer.getLoadingStatus() == 4) || (paramAppletViewer.getLoadingStatus() == 2))
      paramAppletViewer.notifyLoadingDone();
    else
      paramAppletViewer.appletInit();
  }

  public static void initAppletPanel(AppletViewer paramAppletViewer)
  {
    if ((paramAppletViewer.getLoadingStatus() != 4) && (paramAppletViewer.getLoadingStatus() != 2))
      paramAppletViewer.sendAppletInit();
  }

  public static void startAppletPanel(AppletViewer paramAppletViewer)
  {
    paramAppletViewer.appletStart();
  }

  public static void stopAppletPanel(AppletViewer paramAppletViewer)
  {
    paramAppletViewer.appletStop();
  }

  public static void destroyAppletPanel(String paramString, AppletViewer paramAppletViewer)
  {
    if (paramAppletViewer.isLegacyLifeCycle())
    {
      Trace.msgPrintln("lifecycle.applet.support");
      add(paramString, paramAppletViewer);
    }
    else
    {
      paramAppletViewer.appletDestroy();
    }
  }

  public static void cleanupAppletPanel(AppletViewer paramAppletViewer)
  {
    if (!paramAppletViewer.isLegacyLifeCycle())
      paramAppletViewer.cleanup();
  }

  public static void releaseAppletPanel(AppletViewer paramAppletViewer)
  {
    if (!paramAppletViewer.isLegacyLifeCycle())
      paramAppletViewer.release();
  }

  public static void destroyCachedAppletPanels()
  {
    LinkedHashMap localLinkedHashMap;
    synchronized (appletPanels)
    {
      localLinkedHashMap = (LinkedHashMap)appletPanels.clone();
    }
    ??? = localLinkedHashMap.values().iterator();
    Frame localFrame = new Frame();
    localFrame.toFront();
    while (((Iterator)???).hasNext())
    {
      AppletViewer localAppletViewer = (AppletViewer)((Iterator)???).next();
      localFrame.add(localAppletViewer);
      localAppletViewer.appletDestroy();
      localAppletViewer.joinAppletThread();
      localAppletViewer.cleanup();
      localAppletViewer.release();
      localFrame.remove(localAppletViewer);
    }
    localFrame.dispose();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.LifeCycleManager
 * JD-Core Version:    0.6.2
 */