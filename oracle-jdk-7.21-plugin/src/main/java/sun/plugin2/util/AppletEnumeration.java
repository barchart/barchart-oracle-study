package sun.plugin2.util;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketPermission;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.plugin2.applet.Plugin2Manager;

public class AppletEnumeration
{
  private static final Set activeManagers = Collections.synchronizedSet(new HashSet());

  public void setActiveStatus(Plugin2Manager paramPlugin2Manager, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      activeManagers.add(new WeakReference(paramPlugin2Manager));
    }
    else
    {
      ArrayList localArrayList = null;
      synchronized (activeManagers)
      {
        Iterator localIterator = activeManagers.iterator();
        while (localIterator.hasNext())
        {
          WeakReference localWeakReference = (WeakReference)localIterator.next();
          Plugin2Manager localPlugin2Manager = (Plugin2Manager)localWeakReference.get();
          if (localPlugin2Manager == null)
          {
            if (localArrayList == null)
              localArrayList = new ArrayList();
            localArrayList.add(localWeakReference);
          }
          else if (localPlugin2Manager == paramPlugin2Manager)
          {
            activeManagers.remove(localWeakReference);
            break;
          }
        }
      }
      if (localArrayList != null)
      {
        ??? = localArrayList.iterator();
        while (((Iterator)???).hasNext())
          activeManagers.remove(((Iterator)???).next());
      }
    }
  }

  private List getActiveManagers()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (activeManagers)
    {
      Iterator localIterator = activeManagers.iterator();
      while (localIterator.hasNext())
      {
        WeakReference localWeakReference = (WeakReference)localIterator.next();
        Plugin2Manager localPlugin2Manager = (Plugin2Manager)localWeakReference.get();
        if (localPlugin2Manager != null)
          localArrayList.add(localPlugin2Manager);
      }
    }
    return localArrayList;
  }

  public Applet2Adapter getApplet2Adapter(Plugin2Manager paramPlugin2Manager, String paramString)
  {
    List localList = getActiveManagers();
    paramString = paramString.toLowerCase();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Plugin2Manager localPlugin2Manager = (Plugin2Manager)localIterator.next();
      String str = localPlugin2Manager.getParameter("name");
      if (str != null)
        str = str.toLowerCase();
      if ((paramString.equals(str)) && (localPlugin2Manager.getDocumentBase().equals(paramPlugin2Manager.getDocumentBase())))
      {
        try
        {
          if (!checkConnect(paramPlugin2Manager.getCodeBase().getHost(), localPlugin2Manager.getCodeBase().getHost()))
            return null;
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          showStatusText(localInvocationTargetException.getTargetException().getMessage());
          return null;
        }
        catch (Exception localException)
        {
          showStatusText(localException.getMessage());
          return null;
        }
        return localPlugin2Manager.getApplet2Adapter();
      }
    }
    return null;
  }

  public Enumeration getApplet2Adapters(Plugin2Manager paramPlugin2Manager)
  {
    List localList = getActiveManagers();
    ArrayList localArrayList = new ArrayList();
    Object localObject = localList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Plugin2Manager localPlugin2Manager = (Plugin2Manager)((Iterator)localObject).next();
      if (localPlugin2Manager.getDocumentBase().equals(paramPlugin2Manager.getDocumentBase()))
        try
        {
          if (checkConnect(paramPlugin2Manager.getCodeBase().getHost(), localPlugin2Manager.getCodeBase().getHost()))
          {
            Applet2Adapter localApplet2Adapter = localPlugin2Manager.getApplet2Adapter();
            if (localApplet2Adapter.isInstantiated())
              localArrayList.add(localApplet2Adapter);
          }
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          showStatusText(localInvocationTargetException.getTargetException().getMessage());
        }
        catch (Exception localException)
        {
          showStatusText(localException.getMessage());
        }
    }
    localObject = paramPlugin2Manager.getApplet2Adapter();
    if ((((Applet2Adapter)localObject).isInstantiated()) && (!localArrayList.contains(localObject)))
      localArrayList.add(localObject);
    return Collections.enumeration(localArrayList);
  }

  private boolean checkConnect(String paramString1, String paramString2)
    throws Exception
  {
    SocketPermission localSocketPermission1 = new SocketPermission(paramString1, "connect");
    SocketPermission localSocketPermission2 = new SocketPermission(paramString2, "connect");
    return localSocketPermission1.implies(localSocketPermission2);
  }

  private void showStatusText(String paramString)
  {
    Trace.msgPrintln(paramString, null, TraceLevel.BASIC);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.util.AppletEnumeration
 * JD-Core Version:    0.6.2
 */