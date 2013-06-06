package sun.plugin.util;

import java.awt.Component;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class UIUtil
{
  public static void disableBackgroundErase(Component paramComponent)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      private final Component val$component;

      public Object run()
      {
        try
        {
          Toolkit localToolkit = Toolkit.getDefaultToolkit();
          Method localMethod = localToolkit.getClass().getMethod("disableBackgroundErase", new Class[] { Component.class });
          if (localMethod != null)
            localMethod.invoke(localToolkit, new Object[] { this.val$component });
        }
        catch (Exception localException)
        {
        }
        catch (Error localError)
        {
        }
        return null;
      }
    });
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.UIUtil
 * JD-Core Version:    0.6.2
 */