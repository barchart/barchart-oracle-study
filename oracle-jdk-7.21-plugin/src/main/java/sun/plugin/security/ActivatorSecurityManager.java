package sun.plugin.security;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.net.CrossDomainXML;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import sun.applet.AppletSecurity;

public class ActivatorSecurityManager extends AppletSecurity
{
  private final Set lockedThreadGroup = new HashSet();

  public void checkAwtEventQueueAccess()
  {
    try
    {
      super.checkAwtEventQueueAccess();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      checkSecurityAccess("accessEventQueue");
    }
  }

  public void checkSecurityAccess(String paramString)
  {
    if ((paramString != null) && (paramString.equals("java")))
      return;
    super.checkSecurityAccess(paramString);
  }

  public void checkPrintJobAccess()
  {
    try
    {
      super.checkPrintJobAccess();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      new CheckPrint_1_2();
    }
  }

  void showPrintDialog()
  {
    String str1 = ResourceManager.getString("plugin.print.title");
    String str2 = ResourceManager.getString("plugin.print.message");
    String str3 = ResourceManager.getString("plugin.print.always");
    String str4 = (String)ToolkitStore.get().getAppContext().get("sun.plugin.security.printDialog");
    int i = 0;
    if ((!Trace.isAutomationEnabled()) && (str4 == null))
    {
      ToolkitStore.getUI();
      i = ToolkitStore.getUI().showMessageDialog(null, null, 7, str1, null, str2, null, str3, null, null);
    }
    else
    {
      Trace.msgSecurityPrintln("securitymgr.automation.printing");
      ToolkitStore.getUI();
      i = 0;
    }
    ToolkitStore.getUI();
    if (i == 2)
    {
      ToolkitStore.get().getAppContext().put("sun.plugin.security.printDialog", "skip");
    }
    else
    {
      ToolkitStore.getUI();
      if (i != 0)
        throw new SecurityException("checkPrintJobAccess");
    }
  }

  public Class[] getExecutionStackContext()
  {
    return super.getClassContext();
  }

  public synchronized void checkAccess(ThreadGroup paramThreadGroup)
  {
    super.checkAccess(paramThreadGroup);
    if ((paramThreadGroup.parentOf(Thread.currentThread().getThreadGroup())) && (this.lockedThreadGroup.contains(paramThreadGroup)))
      throw new IllegalThreadStateException("forbid thread creation in disposed TG");
  }

  public synchronized void lockThreadGroup(ThreadGroup paramThreadGroup)
  {
    if (paramThreadGroup != null)
      this.lockedThreadGroup.add(paramThreadGroup);
  }

  public synchronized void unlockThreadGroup(ThreadGroup paramThreadGroup)
  {
    if (paramThreadGroup != null)
      this.lockedThreadGroup.remove(paramThreadGroup);
  }

  public void checkConnect(String paramString, int paramInt)
  {
    URL localURL = null;
    int i = paramInt < 0 ? paramInt : -4;
    if ((i == -2) || (i == -3))
      try
      {
        localURL = new URL(paramString);
        paramString = localURL.getHost();
        paramInt = localURL.getPort();
        if (paramInt == -1)
          paramInt = localURL.getDefaultPort();
        if (CrossDomainXML.quickCheck(localURL, paramString, paramInt, i))
          return;
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    try
    {
      super.checkConnect(paramString, paramInt);
    }
    catch (SecurityException localSecurityException)
    {
      if (CrossDomainXML.check(getClassContext(), localURL, paramString, paramInt, i))
        return;
      throw localSecurityException;
    }
  }

  public void checkConnect(String paramString, int paramInt, Object paramObject)
  {
    URL localURL = null;
    int i = paramInt < 0 ? paramInt : -4;
    if ((i == -2) || (i == -3))
      try
      {
        localURL = new URL(paramString);
        paramString = localURL.getHost();
        paramInt = localURL.getPort();
        if (paramInt == -1)
          paramInt = localURL.getDefaultPort();
        if (CrossDomainXML.quickCheck(localURL, paramString, paramInt, i))
          return;
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    try
    {
      super.checkConnect(paramString, paramInt, paramObject);
    }
    catch (SecurityException localSecurityException)
    {
      if (CrossDomainXML.check(getClassContext(), localURL, paramString, paramInt, i))
        return;
      throw localSecurityException;
    }
  }

  private class CheckPrint_1_2
    implements PrivilegedAction
  {
    CheckPrint_1_2()
    {
      AccessController.doPrivileged(this);
    }

    public Object run()
    {
      ActivatorSecurityManager.this.showPrintDialog();
      return null;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.security.ActivatorSecurityManager
 * JD-Core Version:    0.6.2
 */