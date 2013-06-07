package java.security.cert;

public abstract interface CertSelector extends Cloneable
{
  public abstract boolean match(Certificate paramCertificate);

  public abstract Object clone();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.CertSelector
 * JD-Core Version:    0.6.2
 */