package sun.plugin2.applet;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.Permission;
import java.util.HashSet;

public class FXAppletSecurityManager extends SecurityManager
{
  private AppContext mainAppContext;
  private static Field facc = null;
  private static Field fcontext = null;
  private SecurityManager fxSM = new SecurityManager();
  private HashSet restrictedPackages = new HashSet();

  public FXAppletSecurityManager()
  {
    SecurityManagerHelper.resetHelper(this.restrictedPackages);
    this.mainAppContext = ToolkitStore.get().getAppContext();
  }

  public void checkConnect(String paramString, int paramInt)
  {
    SecurityManagerHelper.checkConnectHelper(this.fxSM, paramString, paramInt, getClassContext());
  }

  public void checkConnect(String paramString, int paramInt, Object paramObject)
  {
    SecurityManagerHelper.checkConnectHelper(this.fxSM, paramString, paramInt, paramObject, getClassContext());
  }

  public void checkPermission(Permission paramPermission)
  {
    super.checkPermission(paramPermission);
    SecurityManagerHelper.checkPermissionHelper(paramPermission);
  }

  public void checkAccess(Thread paramThread)
  {
    SecurityManagerHelper.checkAccessHelper(paramThread, this.fxSM, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public void checkAccess(ThreadGroup paramThreadGroup)
  {
    SecurityManagerHelper.checkAccessHelper(paramThreadGroup, this.fxSM, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public void checkAwtEventQueueAccess()
  {
    SecurityManagerHelper.checkAwtEventQueueAccessHelper(this.fxSM, this.mainAppContext, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public void checkPackageAccess(String paramString)
  {
    SecurityManagerHelper.checkPackageAccessHelper(this.fxSM, paramString, this.restrictedPackages);
  }

  public void checkPrintJobAccess()
  {
    SecurityManagerHelper.checkPrintJobAccessHelper(this.fxSM);
  }

  public void checkSecurityAccess(String paramString)
  {
    SecurityManagerHelper.checkSecurityAccessHelper(this.fxSM, paramString);
  }

  public AppContext getAppContext()
  {
    return SecurityManagerHelper.getAppContextHelper(currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public ThreadGroup getThreadGroup()
  {
    return SecurityManagerHelper.getThreadGroupHelper(this.fxSM, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public boolean checkTopLevelWindow(Object paramObject)
  {
    if (paramObject == null)
      throw new NullPointerException("window can't be null");
    return super.checkTopLevelWindow(paramObject);
  }

  static
  {
    try
    {
      facc = URLClassLoader.class.getDeclaredField("acc");
      facc.setAccessible(true);
      fcontext = AccessControlContext.class.getDeclaredField("context");
      fcontext.setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new UnsupportedOperationException(localNoSuchFieldException);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.FXAppletSecurityManager
 * JD-Core Version:    0.6.2
 */