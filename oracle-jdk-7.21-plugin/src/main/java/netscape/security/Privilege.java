package netscape.security;

public final class Privilege
{
  public static final int N_PERMISSIONS = 15;
  public static final int FORBIDDEN = 0;
  public static final int ALLOWED = 1;
  public static final int BLANK = 2;
  public static final int N_DURATIONS = 240;
  public static final int SCOPE = 16;
  public static final int SESSION = 32;
  public static final int FOREVER = 64;
  private int permission = 0;
  private int duration = 16;

  Privilege(int paramInt1, int paramInt2)
  {
    this.permission = paramInt1;
    this.duration = paramInt2;
  }

  public static Privilege findPrivilege(int paramInt1, int paramInt2)
  {
    return new Privilege(paramInt1, paramInt2);
  }

  public static int add(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0))
      return 0;
    if (paramInt1 == 2)
      return paramInt2;
    if (paramInt2 == 2)
      return paramInt1;
    if ((paramInt1 == 1) || (paramInt2 == 1))
      return 1;
    return 2;
  }

  public static Privilege add(Privilege paramPrivilege1, Privilege paramPrivilege2)
  {
    int i = add(paramPrivilege1.getPermission(), paramPrivilege2.getPermission());
    return new Privilege(i, paramPrivilege1.getDuration());
  }

  public boolean samePermission(Privilege paramPrivilege)
  {
    return samePermission(paramPrivilege.getPermission());
  }

  public boolean samePermission(int paramInt)
  {
    return this.permission == paramInt;
  }

  public boolean sameDuration(Privilege paramPrivilege)
  {
    return sameDuration(paramPrivilege.getDuration());
  }

  public boolean sameDuration(int paramInt)
  {
    return this.duration == paramInt;
  }

  public boolean isAllowed()
  {
    return this.permission == 1;
  }

  public boolean isForbidden()
  {
    return this.permission == 0;
  }

  public boolean isBlank()
  {
    return this.permission == 2;
  }

  public int getPermission()
  {
    return this.permission;
  }

  public int getDuration()
  {
    return this.duration;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Privilege))
    {
      Privilege localPrivilege = (Privilege)paramObject;
      return (this.permission == localPrivilege.getPermission()) && (this.duration == localPrivilege.getDuration());
    }
    return false;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.security.Privilege
 * JD-Core Version:    0.6.2
 */