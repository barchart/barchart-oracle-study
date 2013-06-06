package sun.plugin.liveconnect;

import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public class JavaScriptProtectionDomain extends ProtectionDomain
{
  PermissionCollection perms = null;

  public JavaScriptProtectionDomain(PermissionCollection paramPermissionCollection)
  {
    super(null, null);
    this.perms = paramPermissionCollection;
  }

  public boolean implies(Permission paramPermission)
  {
    return this.perms.implies(paramPermission);
  }

  public String toString()
  {
    return "JavaScriptProtectionDomain " + getCodeSource() + "\n" + getPermissions() + "\n";
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.JavaScriptProtectionDomain
 * JD-Core Version:    0.6.2
 */