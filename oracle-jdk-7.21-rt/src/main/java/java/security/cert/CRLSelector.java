package java.security.cert;

public abstract interface CRLSelector extends Cloneable
{
  public abstract boolean match(CRL paramCRL);

  public abstract Object clone();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.CRLSelector
 * JD-Core Version:    0.6.2
 */