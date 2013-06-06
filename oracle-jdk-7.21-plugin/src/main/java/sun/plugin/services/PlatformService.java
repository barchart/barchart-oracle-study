package sun.plugin.services;

import com.sun.deploy.trace.Trace;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class PlatformService
{
  private static PlatformService ps = null;

  public int createEvent()
  {
    return 0;
  }

  public void deleteEvent(int paramInt)
  {
  }

  public void signalEvent(int paramInt)
  {
  }

  public void waitEvent(int paramInt)
  {
  }

  public void waitEvent(long paramLong, int paramInt)
  {
    waitEvent(paramInt);
  }

  public void waitEvent(long paramLong1, int paramInt, long paramLong2)
  {
    waitEvent(paramInt);
  }

  public void dispatchNativeEvent()
  {
  }

  public static synchronized PlatformService getService()
  {
    if (ps == null)
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      try
      {
        String str2 = null;
        if (str1.indexOf("Windows") != -1)
          str2 = "sun.plugin.services.WPlatformService";
        else if (str1.indexOf("OS X") != -1)
          str2 = "sun.plugin.services.MacOSXPlatformService";
        else
          str2 = "sun.plugin.services.MPlatformService";
        Class localClass = Class.forName(str2);
        if (localClass != null)
        {
          Object localObject = localClass.newInstance();
          if ((localObject instanceof PlatformService))
            ps = (PlatformService)localObject;
        }
      }
      catch (Exception localException)
      {
        Trace.printException(localException);
      }
    }
    return ps;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.services.PlatformService
 * JD-Core Version:    0.6.2
 */