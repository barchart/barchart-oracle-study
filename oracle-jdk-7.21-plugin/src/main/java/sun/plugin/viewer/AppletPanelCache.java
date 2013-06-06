package sun.plugin.viewer;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import sun.applet.AppletPanel;

public class AppletPanelCache
{
  private static HashMap appletPanels = new HashMap();

  public static Object[] getAppletPanels()
  {
    synchronized (appletPanels)
    {
      ArrayList localArrayList = new ArrayList();
      Collection localCollection = appletPanels.values();
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        SoftReference localSoftReference = (SoftReference)localIterator.next();
        if (localSoftReference != null)
        {
          AppletPanel localAppletPanel = (AppletPanel)localSoftReference.get();
          if (localAppletPanel != null)
            localArrayList.add(localAppletPanel);
        }
      }
      return localArrayList.toArray();
    }
  }

  public static void add(AppletPanel paramAppletPanel)
  {
    synchronized (appletPanels)
    {
      appletPanels.put(new Integer(paramAppletPanel.hashCode()), new SoftReference(paramAppletPanel));
    }
  }

  public static void remove(AppletPanel paramAppletPanel)
  {
    synchronized (appletPanels)
    {
      appletPanels.remove(new Integer(paramAppletPanel.hashCode()));
    }
  }

  public static boolean hasValidInstance()
  {
    synchronized (appletPanels)
    {
      Collection localCollection = appletPanels.values();
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        SoftReference localSoftReference = (SoftReference)localIterator.next();
        if ((localSoftReference != null) && (localSoftReference.get() != null))
          return true;
      }
      return false;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.AppletPanelCache
 * JD-Core Version:    0.6.2
 */