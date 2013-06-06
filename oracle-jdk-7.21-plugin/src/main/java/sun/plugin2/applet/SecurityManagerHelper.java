package sun.plugin2.applet;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.net.CrossDomainXML;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import sun.security.util.SecurityConstants;

public class SecurityManagerHelper
{
  private static final ThreadLocal inThreadGroupCheck = new ThreadLocal()
  {
    protected Object initialValue()
    {
      return Boolean.FALSE;
    }
  };

  static void resetHelper(HashSet paramHashSet)
  {
    paramHashSet.clear();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      private final HashSet val$restrictedPackages;

      public Object run()
      {
        Enumeration localEnumeration = System.getProperties().propertyNames();
        while (localEnumeration.hasMoreElements())
        {
          String str1 = (String)localEnumeration.nextElement();
          if ((str1 != null) && (str1.startsWith("package.restrict.access.")))
          {
            String str2 = System.getProperty(str1);
            if ((str2 != null) && (str2.equalsIgnoreCase("true")))
            {
              String str3 = str1.substring(24);
              this.val$restrictedPackages.add(str3);
            }
          }
        }
        return null;
      }
    });
  }

  static void checkAccessHelper(Thread paramThread, SecurityManager paramSecurityManager, ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, Field paramField2)
  {
    if ((!isThreadTerminated(paramThread)) && (!inThreadGroup(paramSecurityManager, paramThread, paramClassLoader, paramArrayOfClass, paramField1, paramField2)))
      paramSecurityManager.checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
  }

  static void checkAccessHelper(ThreadGroup paramThreadGroup, SecurityManager paramSecurityManager, ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, Field paramField2)
  {
    if (inThreadGroupCheck.get().equals(Boolean.TRUE))
      paramSecurityManager.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
    else
      try
      {
        inThreadGroupCheck.set(Boolean.TRUE);
        if (!inThreadGroup(paramSecurityManager, paramThreadGroup, paramClassLoader, paramArrayOfClass, paramField1, paramField2))
          paramSecurityManager.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
      }
      finally
      {
        inThreadGroupCheck.set(Boolean.FALSE);
      }
  }

  private static boolean isThreadTerminated(Thread paramThread)
  {
    try
    {
      return paramThread.getState() == Thread.State.TERMINATED;
    }
    catch (Throwable localThrowable)
    {
      if (!paramThread.isAlive())
        tmpTernaryOp = true;
    }
    return false;
  }

  private static Plugin2ClassLoader currentAppletClassLoader(ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, final Field paramField2)
  {
    if ((paramClassLoader == null) || ((paramClassLoader instanceof Plugin2ClassLoader)))
      return (Plugin2ClassLoader)paramClassLoader;
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      paramClassLoader = paramArrayOfClass[i].getClassLoader();
      if ((paramClassLoader instanceof Plugin2ClassLoader))
        return (Plugin2ClassLoader)paramClassLoader;
    }
    for (i = 0; i < paramArrayOfClass.length; i++)
    {
      final ClassLoader localClassLoader = paramArrayOfClass[i].getClassLoader();
      if ((localClassLoader instanceof URLClassLoader))
      {
        paramClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
        {
          private final Field val$facc;
          private final ClassLoader val$currentLoader;
          private final Field val$fcontext;

          public Object run()
          {
            AccessControlContext localAccessControlContext = null;
            ProtectionDomain[] arrayOfProtectionDomain = null;
            try
            {
              localAccessControlContext = (AccessControlContext)this.val$facc.get(localClassLoader);
              if (localAccessControlContext == null)
                return null;
              arrayOfProtectionDomain = (ProtectionDomain[])paramField2.get(localAccessControlContext);
              if (arrayOfProtectionDomain == null)
                return null;
            }
            catch (Exception localException)
            {
              throw new UnsupportedOperationException(localException);
            }
            for (int i = 0; i < arrayOfProtectionDomain.length; i++)
            {
              ClassLoader localClassLoader = arrayOfProtectionDomain[i].getClassLoader();
              if ((localClassLoader instanceof Plugin2ClassLoader))
                return localClassLoader;
            }
            return null;
          }
        });
        if (paramClassLoader != null)
          return (Plugin2ClassLoader)paramClassLoader;
      }
    }
    paramClassLoader = Thread.currentThread().getContextClassLoader();
    if ((paramClassLoader instanceof Plugin2ClassLoader))
      return (Plugin2ClassLoader)paramClassLoader;
    return (Plugin2ClassLoader)null;
  }

  private static boolean inThreadGroup(SecurityManager paramSecurityManager, ThreadGroup paramThreadGroup, ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, Field paramField2)
  {
    if (currentAppletClassLoader(paramClassLoader, paramArrayOfClass, paramField1, paramField2) == null)
      return false;
    ThreadGroup localThreadGroup = getThreadGroupHelper(paramSecurityManager, paramClassLoader, paramArrayOfClass, paramField1, paramField2);
    if (localThreadGroup == null)
      return false;
    return localThreadGroup.parentOf(paramThreadGroup);
  }

  private static boolean inThreadGroup(SecurityManager paramSecurityManager, Thread paramThread, ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, Field paramField2)
  {
    return inThreadGroup(paramSecurityManager, paramThread.getThreadGroup(), paramClassLoader, paramArrayOfClass, paramField1, paramField2);
  }

  static void checkPackageAccessHelper(SecurityManager paramSecurityManager, String paramString, HashSet paramHashSet)
  {
    paramSecurityManager.checkPackageAccess(paramString);
    Iterator localIterator = paramHashSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((paramString.equals(str)) || (paramString.startsWith(str + ".")))
        paramSecurityManager.checkPermission(new RuntimePermission("accessClassInPackage." + paramString));
    }
  }

  static void checkConnectHelper(SecurityManager paramSecurityManager, String paramString, int paramInt, Object paramObject, Class[] paramArrayOfClass)
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
      paramSecurityManager.checkConnect(paramString, paramInt, paramObject);
    }
    catch (SecurityException localSecurityException)
    {
      if (CrossDomainXML.check(paramArrayOfClass, localURL, paramString, paramInt, i))
        return;
      throw localSecurityException;
    }
  }

  static void checkConnectHelper(SecurityManager paramSecurityManager, String paramString, int paramInt, Class[] paramArrayOfClass)
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
      paramSecurityManager.checkConnect(paramString, paramInt);
    }
    catch (SecurityException localSecurityException)
    {
      if (CrossDomainXML.check(paramArrayOfClass, localURL, paramString, paramInt, i))
        return;
      throw localSecurityException;
    }
  }

  static void checkAwtEventQueueAccessHelper(SecurityManager paramSecurityManager, AppContext paramAppContext, ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, Field paramField2)
  {
    try
    {
      AppContext localAppContext = ToolkitStore.get().getAppContext();
      Plugin2ClassLoader localPlugin2ClassLoader = currentAppletClassLoader(paramClassLoader, paramArrayOfClass, paramField1, paramField2);
      if ((localAppContext.equals(paramAppContext)) && (localPlugin2ClassLoader != null))
        paramSecurityManager.checkAwtEventQueueAccess();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      paramSecurityManager.checkSecurityAccess("accessEventQueue");
    }
  }

  static void checkPrintJobAccessHelper(SecurityManager paramSecurityManager)
  {
    try
    {
      paramSecurityManager.checkPrintJobAccess();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          SecurityManagerHelper.access$000();
          return null;
        }
      });
    }
  }

  private static void showPrintDialog()
  {
    String str1 = ResourceManager.getString("plugin.print.title");
    String str2 = ResourceManager.getString("plugin.print.message");
    String str3 = ResourceManager.getString("plugin.print.always");
    String str4 = (String)ToolkitStore.get().getAppContext().get("sun.plugin.security.printDialog");
    int i;
    if ((!Trace.isAutomationEnabled()) && (str4 == null))
    {
      i = ToolkitStore.getUI().showMessageDialog(null, null, 7, str1, null, str2, null, null, str3, null);
    }
    else
    {
      Trace.msgSecurityPrintln("securitymgr.automation.printing");
      i = 0;
    }
    if (i != 0)
      throw new SecurityException("checkPrintJobAccess");
    ToolkitStore.get().getAppContext().put("sun.plugin.security.printDialog", "skip");
    if (i == 2);
  }

  static void checkSecurityAccessHelper(SecurityManager paramSecurityManager, String paramString)
  {
    if ((paramString != null) && (paramString.equals("java")))
      return;
    paramSecurityManager.checkSecurityAccess(paramString);
  }

  static AppContext getAppContextHelper(ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, Field paramField2)
  {
    Plugin2ClassLoader localPlugin2ClassLoader = currentAppletClassLoader(paramClassLoader, paramArrayOfClass, paramField1, paramField2);
    if (localPlugin2ClassLoader == null)
      return null;
    AppContext localAppContext = localPlugin2ClassLoader.getAppContext();
    if (localAppContext == null)
      throw new SecurityException("Applet classloader has invalid AppContext");
    return localAppContext;
  }

  static ThreadGroup getThreadGroupHelper(SecurityManager paramSecurityManager, ClassLoader paramClassLoader, Class[] paramArrayOfClass, Field paramField1, Field paramField2)
  {
    Plugin2ClassLoader localPlugin2ClassLoader = currentAppletClassLoader(paramClassLoader, paramArrayOfClass, paramField1, paramField2);
    ThreadGroup localThreadGroup = localPlugin2ClassLoader == null ? null : localPlugin2ClassLoader.getThreadGroup();
    if (localThreadGroup != null)
      return localThreadGroup;
    return paramSecurityManager.getThreadGroup();
  }

  static void checkPermissionHelper(Permission paramPermission)
  {
    if (paramPermission.getName().equals("setSecurityManager"))
    {
      Applet2ExecutionContext localApplet2ExecutionContext = Plugin2Manager.getCurrentAppletExecutionContext();
      if (!localApplet2ExecutionContext.requestCustomSecurityManager())
        throw new SecurityException("JVM Shared, not allowed to set security manager");
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.SecurityManagerHelper
 * JD-Core Version:    0.6.2
 */