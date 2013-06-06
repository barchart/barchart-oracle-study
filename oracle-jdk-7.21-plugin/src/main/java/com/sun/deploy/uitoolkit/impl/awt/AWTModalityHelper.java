package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.impl.awt.ui.AWTDialog;
import com.sun.deploy.uitoolkit.ui.AbstractDialog;
import com.sun.deploy.uitoolkit.ui.ModalityHelper;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.LinkedList;
import sun.awt.ModalityEvent;
import sun.awt.ModalityListener;
import sun.awt.SunToolkit;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.main.client.ModalityInterface;
import sun.plugin2.os.windows.FLASHWINFO;
import sun.plugin2.os.windows.Windows;
import sun.plugin2.util.SystemUtil;

public class AWTModalityHelper
  implements ModalityHelper
{
  private final LinkedList managersShowingSystemDialogs = new LinkedList();
  private static Reactivator reactivator;

  public boolean installModalityListener(ModalityInterface paramModalityInterface)
  {
    try
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      if ((localToolkit instanceof SunToolkit))
      {
        SunToolkit localSunToolkit = (SunToolkit)localToolkit;
        localSunToolkit.addModalityListener(new PluginModalityListener(paramModalityInterface));
        Trace.println("Added plugin modalityListener", TraceLevel.UI);
        return true;
      }
      Trace.println("Not a SunToolkit: " + localToolkit, TraceLevel.UI);
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
    return false;
  }

  public void pushManagerShowingSystemDialog()
  {
    Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
    synchronized (this.managersShowingSystemDialogs)
    {
      this.managersShowingSystemDialogs.addFirst(localPlugin2Manager);
    }
  }

  public Plugin2Manager getManagerShowingSystemDialog()
  {
    synchronized (this.managersShowingSystemDialogs)
    {
      if (!this.managersShowingSystemDialogs.isEmpty())
        return (Plugin2Manager)this.managersShowingSystemDialogs.getFirst();
    }
    return null;
  }

  public void popManagerShowingSystemDialog()
  {
    Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
    synchronized (this.managersShowingSystemDialogs)
    {
      Iterator localIterator = this.managersShowingSystemDialogs.iterator();
      while (localIterator.hasNext())
        if (localIterator.next() == localPlugin2Manager)
          localIterator.remove();
    }
  }

  public void reactivateDialog(AbstractDialog paramAbstractDialog)
  {
    if ((paramAbstractDialog instanceof AWTDialog))
      getReactivator().reactivate(((AWTDialog)paramAbstractDialog).getDialog());
    else
      Trace.println("reactivateDialog: not an AWTDialog");
  }

  private Reactivator getReactivator()
  {
    if (reactivator == null)
      if (SystemUtil.getOSType() == 1)
        reactivator = new WindowsReactivator();
      else
        reactivator = new NoopReactivator();
    return reactivator;
  }

  static class NoopReactivator
    implements AWTModalityHelper.Reactivator
  {
    public void reactivate(Dialog paramDialog)
    {
    }
  }

  static class PluginModalityListener
    implements ModalityListener
  {
    private ModalityInterface modality;

    PluginModalityListener(ModalityInterface paramModalityInterface)
    {
      this.modality = paramModalityInterface;
    }

    public void modalityPushed(ModalityEvent paramModalityEvent)
    {
      this.modality.modalityPushed(AWTDialog.getAWTDialog((Dialog)paramModalityEvent.getSource()));
    }

    public void modalityPopped(ModalityEvent paramModalityEvent)
    {
      this.modality.modalityPopped(AWTDialog.getAWTDialog((Dialog)paramModalityEvent.getSource()));
    }
  }

  static abstract interface Reactivator
  {
    public abstract void reactivate(Dialog paramDialog);
  }

  static class WindowsReactivator
    implements AWTModalityHelper.Reactivator
  {
    private Method getHWndMethod;

    public void reactivate(Dialog paramDialog)
    {
      if (this.getHWndMethod == null)
        try
        {
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Object run()
            {
              try
              {
                Class localClass = Class.forName("sun.awt.windows.WComponentPeer");
                Method localMethod = localClass.getDeclaredMethod("getHWnd", null);
                localMethod.setAccessible(true);
                AWTModalityHelper.WindowsReactivator.this.getHWndMethod = localMethod;
              }
              catch (Exception localException)
              {
              }
              return null;
            }
          });
        }
        catch (Exception localException1)
        {
        }
      long l = 0L;
      try
      {
        l = ((Long)this.getHWndMethod.invoke(paramDialog.getPeer(), null)).longValue();
        Windows.MessageBeep(0);
        FLASHWINFO localFLASHWINFO = FLASHWINFO.create();
        localFLASHWINFO.cbSize(FLASHWINFO.size());
        localFLASHWINFO.hwnd(l);
        localFLASHWINFO.dwFlags(1);
        localFLASHWINFO.uCount(3);
        localFLASHWINFO.dwTimeout(64);
        Windows.FlashWindowEx(localFLASHWINFO);
      }
      catch (Exception localException2)
      {
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTModalityHelper
 * JD-Core Version:    0.6.2
 */