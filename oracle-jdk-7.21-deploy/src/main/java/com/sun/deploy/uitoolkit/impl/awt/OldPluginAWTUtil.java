package com.sun.deploy.uitoolkit.impl.awt;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.lang.reflect.InvocationTargetException;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class OldPluginAWTUtil
{
  public static void postEvent(Component paramComponent, AWTEvent paramAWTEvent)
  {
    AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
    if (localAppContext != null)
      SunToolkit.postEvent(localAppContext, paramAWTEvent);
  }

  public static void invokeLater(Component paramComponent, Runnable paramRunnable)
  {
    AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
    if (localAppContext != null)
      SunToolkit.postEvent(localAppContext, new InvocationEvent(paramComponent, paramRunnable));
  }

  public static void invokeLater(AppContext paramAppContext, Runnable paramRunnable)
  {
    if (paramAppContext != null)
      SunToolkit.postEvent(paramAppContext, new InvocationEvent(Toolkit.getDefaultToolkit(), paramRunnable));
  }

  public static void invokeAndWait(Component paramComponent, Runnable paramRunnable)
    throws InterruptedException, InvocationTargetException
  {
    if (EventQueue.isDispatchThread())
      throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
    Object local1AWTInvocationLock = new Object()
    {
    };
    InvocationEvent localInvocationEvent = new InvocationEvent(paramComponent, paramRunnable, local1AWTInvocationLock, true);
    synchronized (local1AWTInvocationLock)
    {
      AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
      if (localAppContext != null)
      {
        SunToolkit.postEvent(localAppContext, localInvocationEvent);
        local1AWTInvocationLock.wait();
      }
    }
    ??? = localInvocationEvent.getException();
    if (??? != null)
      throw new InvocationTargetException((Throwable)???);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.OldPluginAWTUtil
 * JD-Core Version:    0.6.2
 */