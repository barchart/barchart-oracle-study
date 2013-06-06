package sun.plugin.liveconnect;

import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AllPermission;

public class SecurityContextHelper
{
  public static boolean Implies(AccessControlContext paramAccessControlContext, String paramString1, String paramString2)
  {
    if ((paramAccessControlContext == null) || (paramString1 == null))
      return false;
    try
    {
      if (paramString1.equals("AllJavaPermission"))
        paramAccessControlContext.checkPermission(new AllPermission());
      else
        paramAccessControlContext.checkPermission(new JavaScriptPermission(paramString1));
    }
    catch (AccessControlException localAccessControlException)
    {
      return false;
    }
    return true;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.SecurityContextHelper
 * JD-Core Version:    0.6.2
 */