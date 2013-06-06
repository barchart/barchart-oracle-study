package sun.plugin2.applet;

import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.uitoolkit.impl.awt.AWTAppContext;
import com.sun.deploy.uitoolkit.impl.awt.AWTDragHelper.DraggedAppletFrame;
import com.sun.deploy.uitoolkit.impl.awt.AWTDragHelper.DraggedAppletJFrame;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.Permission;
import java.util.HashSet;
import sun.awt.AWTSecurityManager;

public class AWTAppletSecurityManager extends AWTSecurityManager
{
  private com.sun.deploy.appcontext.AppContext mainAppContext;
  private static Field facc = null;
  private static Field fcontext = null;
  private SecurityManager awtSM = new AWTSecurityManager();
  private HashSet restrictedPackages = new HashSet();

  public AWTAppletSecurityManager()
  {
    SecurityManagerHelper.resetHelper(this.restrictedPackages);
    this.mainAppContext = ToolkitStore.get().getAppContext();
  }

  public void checkConnect(String paramString, int paramInt)
  {
    SecurityManagerHelper.checkConnectHelper(this.awtSM, paramString, paramInt, getClassContext());
  }

  public void checkConnect(String paramString, int paramInt, Object paramObject)
  {
    SecurityManagerHelper.checkConnectHelper(this.awtSM, paramString, paramInt, paramObject, getClassContext());
  }

  public void checkPermission(Permission paramPermission)
  {
    super.checkPermission(paramPermission);
    SecurityManagerHelper.checkPermissionHelper(paramPermission);
  }

  public void checkAccess(Thread paramThread)
  {
    SecurityManagerHelper.checkAccessHelper(paramThread, this.awtSM, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public void checkAccess(ThreadGroup paramThreadGroup)
  {
    SecurityManagerHelper.checkAccessHelper(paramThreadGroup, this.awtSM, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public void checkAwtEventQueueAccess()
  {
    SecurityManagerHelper.checkAwtEventQueueAccessHelper(this.awtSM, this.mainAppContext, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public void checkPackageAccess(String paramString)
  {
    SecurityManagerHelper.checkPackageAccessHelper(this.awtSM, paramString, this.restrictedPackages);
  }

  public void checkPrintJobAccess()
  {
    SecurityManagerHelper.checkPrintJobAccessHelper(this.awtSM);
  }

  public void checkSecurityAccess(String paramString)
  {
    SecurityManagerHelper.checkSecurityAccessHelper(this.awtSM, paramString);
  }

  public sun.awt.AppContext getAppContext()
  {
    com.sun.deploy.appcontext.AppContext localAppContext = SecurityManagerHelper.getAppContextHelper(currentClassLoader(), getClassContext(), facc, fcontext);
    if ((localAppContext instanceof AWTAppContext))
      return ((AWTAppContext)localAppContext).getAWTAppContext();
    return null;
  }

  public ThreadGroup getThreadGroup()
  {
    return SecurityManagerHelper.getThreadGroupHelper(this.awtSM, currentClassLoader(), getClassContext(), facc, fcontext);
  }

  public boolean checkTopLevelWindow(Object paramObject)
  {
    if (paramObject == null)
      throw new NullPointerException("window can't be null");
    if (((paramObject instanceof AWTDragHelper.DraggedAppletJFrame)) || ((paramObject instanceof AWTDragHelper.DraggedAppletFrame)))
      return false;
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
 * Qualified Name:     sun.plugin2.applet.AWTAppletSecurityManager
 * JD-Core Version:    0.6.2
 */