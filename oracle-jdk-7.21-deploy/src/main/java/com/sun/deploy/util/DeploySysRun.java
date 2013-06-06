package com.sun.deploy.util;

import com.sun.deploy.trace.Trace;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class DeploySysRun
{
  private static volatile DeploySysRun delegate;

  public static void setOverride(DeploySysRun paramDeploySysRun)
  {
    delegate = paramDeploySysRun;
  }

  public static Object execute(DeploySysAction paramDeploySysAction)
    throws Exception
  {
    DeploySysRun localDeploySysRun = delegate;
    Object localObject = localDeploySysRun != null ? localDeploySysRun.delegate(paramDeploySysAction) : paramDeploySysAction.execute();
    return localObject;
  }

  public static Object execute(DeploySysAction paramDeploySysAction, Object paramObject)
  {
    try
    {
      return execute(paramDeploySysAction);
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
    }
    return paramObject;
  }

  public static Object executePrivileged(DeploySysAction paramDeploySysAction, Object paramObject)
  {
    return AccessController.doPrivileged(new PrivilegedAction()
    {
      private final DeploySysAction val$action;
      private final Object val$defaultValue;

      public Object run()
      {
        try
        {
          return DeploySysRun.execute(this.val$action);
        }
        catch (Exception localException)
        {
          Trace.ignoredException(localException);
        }
        return this.val$defaultValue;
      }
    });
  }

  protected abstract Object delegate(DeploySysAction paramDeploySysAction)
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.DeploySysRun
 * JD-Core Version:    0.6.2
 */