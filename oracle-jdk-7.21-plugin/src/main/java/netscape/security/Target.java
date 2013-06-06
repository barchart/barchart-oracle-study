package netscape.security;

public class Target
{
  private String name = null;
  private Principal prin = null;
  private String risk = null;
  private String riskColor = null;
  private String description = null;
  private String url = null;
  private Target[] targetAry = null;

  protected Target()
  {
    this(null, null, null, null, null, null, null);
  }

  public Target(String paramString, Principal paramPrincipal)
  {
    this(paramString, paramPrincipal, null, null, null, null, null);
  }

  public Target(String paramString)
  {
    this(paramString, null, null, null, null, null, null);
  }

  public Target(String paramString, Principal paramPrincipal, Target[] paramArrayOfTarget)
  {
    this(paramString, paramPrincipal, null, null, null, null, paramArrayOfTarget);
  }

  public Target(String paramString1, Principal paramPrincipal, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    this(paramString1, paramPrincipal, paramString2, paramString3, paramString4, paramString5, null);
  }

  public Target(String paramString1, Principal paramPrincipal, String paramString2, String paramString3, String paramString4, String paramString5, Target[] paramArrayOfTarget)
  {
    this.name = paramString1;
    this.prin = paramPrincipal;
    this.risk = paramString2;
    this.riskColor = paramString3;
    this.description = paramString4;
    this.url = paramString5;
    this.targetAry = paramArrayOfTarget;
  }

  public final Target registerTarget()
  {
    return this;
  }

  public static Target findTarget(String paramString)
  {
    return new Target(paramString);
  }

  public static Target findTarget(String paramString, Principal paramPrincipal)
  {
    return new Target(paramString, paramPrincipal);
  }

  public static Target findTarget(Target paramTarget)
  {
    return paramTarget;
  }

  public Privilege checkPrivilegeEnabled(Principal[] paramArrayOfPrincipal, Object paramObject)
  {
    PrivilegeManager localPrivilegeManager = PrivilegeManager.getPrivilegeManager();
    localPrivilegeManager.checkPrivilegeEnabled(this, paramObject);
    return null;
  }

  public Privilege checkPrivilegeEnabled(Principal[] paramArrayOfPrincipal)
  {
    return checkPrivilegeEnabled(paramArrayOfPrincipal, null);
  }

  public Privilege checkPrivilegeEnabled(Principal paramPrincipal, Object paramObject)
  {
    PrivilegeManager localPrivilegeManager = PrivilegeManager.getPrivilegeManager();
    localPrivilegeManager.checkPrivilegeEnabled(this, paramObject);
    return null;
  }

  public Privilege enablePrivilege(Principal paramPrincipal, Object paramObject)
  {
    PrivilegeManager localPrivilegeManager = PrivilegeManager.getPrivilegeManager();
    localPrivilegeManager.enablePrivilege(this, paramPrincipal, paramObject);
    return null;
  }

  public String getRisk()
  {
    return this.risk;
  }

  public String getRiskColor()
  {
    return this.riskColor;
  }

  public String getDescription()
  {
    return this.description;
  }

  public static Target getTargetFromDescription(String paramString)
  {
    return null;
  }

  public String getHelpUrl()
  {
    return this.url;
  }

  public String getDetailedInfo(Object paramObject)
  {
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.security.Target
 * JD-Core Version:    0.6.2
 */