package java.security.cert;

public abstract interface CertPathBuilderResult extends Cloneable
{
  public abstract CertPath getCertPath();

  public abstract Object clone();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.CertPathBuilderResult
 * JD-Core Version:    0.6.2
 */