package java.security;

public abstract interface DomainCombiner
{
  public abstract ProtectionDomain[] combine(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.DomainCombiner
 * JD-Core Version:    0.6.2
 */