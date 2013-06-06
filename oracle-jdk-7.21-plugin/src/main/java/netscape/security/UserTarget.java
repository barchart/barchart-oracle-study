package netscape.security;

public class UserTarget extends Target
{
  public UserTarget()
  {
  }

  public UserTarget(String paramString1, Principal paramPrincipal, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    super(paramString1, paramPrincipal, paramString2, paramString3, paramString4, paramString5);
  }

  public UserTarget(String paramString1, Principal paramPrincipal, String paramString2, String paramString3, String paramString4, String paramString5, Target[] paramArrayOfTarget)
  {
    super(paramString1, paramPrincipal, paramString2, paramString3, paramString4, paramString5, paramArrayOfTarget);
  }

  public Privilege enablePrivilege(Principal paramPrincipal, Object paramObject)
  {
    PrivilegeManager localPrivilegeManager = PrivilegeManager.getPrivilegeManager();
    localPrivilegeManager.enablePrivilege(this, paramPrincipal, paramObject);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.security.UserTarget
 * JD-Core Version:    0.6.2
 */