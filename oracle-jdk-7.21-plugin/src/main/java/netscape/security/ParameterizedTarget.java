package netscape.security;

public class ParameterizedTarget extends UserTarget
{
  protected ParameterizedTarget()
  {
  }

  public ParameterizedTarget(String paramString1, Principal paramPrincipal, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    super(paramString1, paramPrincipal, paramString2, paramString3, paramString4, paramString5);
  }

  public String getDetailedInfo(Object paramObject)
  {
    return null;
  }

  public Privilege enablePrivilege(Principal paramPrincipal, Object paramObject)
  {
    PrivilegeManager localPrivilegeManager = PrivilegeManager.getPrivilegeManager();
    localPrivilegeManager.enablePrivilege(this, paramPrincipal, paramObject);
    return null;
  }

  public Privilege checkPrivilegeEnabled(Principal[] paramArrayOfPrincipal, Object paramObject)
  {
    PrivilegeManager localPrivilegeManager = PrivilegeManager.getPrivilegeManager();
    localPrivilegeManager.checkPrivilegeEnabled(this, paramObject);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.security.ParameterizedTarget
 * JD-Core Version:    0.6.2
 */