package netscape.security;

import com.sun.deploy.resources.ResourceManager;
import java.io.PrintStream;

public class PrivilegeManager
{
  public static final int PROPER_SUBSET = 1;
  public static final int EQUAL = 2;
  public static final int NO_SUBSET = 3;
  public static final int SIGNED_APPLET_DBNAME = 4;
  public static final int TEMP_FILENAME = 5;

  public void checkPrivilegeEnabled(Target paramTarget)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void checkPrivilegeEnabled(Target paramTarget, Object paramObject)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public static void enablePrivilege(String paramString)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void enablePrivilege(Target paramTarget)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void enablePrivilege(Target paramTarget, Principal paramPrincipal)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void enablePrivilege(Target paramTarget, Principal paramPrincipal, Object paramObject)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void revertPrivilege(Target paramTarget)
  {
    printErrorMessage();
  }

  public static void revertPrivilege(String paramString)
  {
    printErrorMessage();
  }

  public void disablePrivilege(Target paramTarget)
  {
    printErrorMessage();
  }

  public static void disablePrivilege(String paramString)
  {
    printErrorMessage();
  }

  public static void checkPrivilegeGranted(String paramString)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void checkPrivilegeGranted(Target paramTarget)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void checkPrivilegeGranted(Target paramTarget, Object paramObject)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public void checkPrivilegeGranted(Target paramTarget, Principal paramPrincipal, Object paramObject)
    throws ForbiddenTargetException
  {
    printErrorMessage();
  }

  public boolean isCalledByPrincipal(Principal paramPrincipal, int paramInt)
  {
    printErrorMessage();
    return false;
  }

  public boolean isCalledByPrincipal(Principal paramPrincipal)
  {
    printErrorMessage();
    return isCalledByPrincipal(paramPrincipal, 1);
  }

  public static Principal getSystemPrincipal()
  {
    printErrorMessage();
    return null;
  }

  public static PrivilegeManager getPrivilegeManager()
  {
    printErrorMessage();
    return new PrivilegeManager();
  }

  public static Principal[] getMyPrincipals()
  {
    printErrorMessage();
    return null;
  }

  public Principal[] getClassPrincipals(Class paramClass)
  {
    printErrorMessage();
    return null;
  }

  public boolean hasPrincipal(Class paramClass, Principal paramPrincipal)
  {
    printErrorMessage();
    return false;
  }

  public int comparePrincipalArray(Principal[] paramArrayOfPrincipal1, Principal[] paramArrayOfPrincipal2)
  {
    printErrorMessage();
    return 3;
  }

  public boolean checkMatchPrincipal(Class paramClass, int paramInt)
  {
    printErrorMessage();
    return false;
  }

  public boolean checkMatchPrincipal(Principal paramPrincipal, int paramInt)
  {
    printErrorMessage();
    return false;
  }

  public boolean checkMatchPrincipal(Class paramClass)
  {
    printErrorMessage();
    return false;
  }

  public boolean checkMatchPrincipalAlways()
  {
    printErrorMessage();
    return false;
  }

  public Principal[] getClassPrincipalsFromStack(int paramInt)
  {
    printErrorMessage();
    return null;
  }

  public PrivilegeTable getPrivilegeTableFromStack()
  {
    printErrorMessage();
    return null;
  }

  private static void printErrorMessage()
  {
    System.err.println(ResourceManager.getMessage("liveconnect.wrong.securitymodel"));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.security.PrivilegeManager
 * JD-Core Version:    0.6.2
 */