package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathBuilderSpi
{
  public abstract CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters)
    throws CertPathBuilderException, InvalidAlgorithmParameterException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.CertPathBuilderSpi
 * JD-Core Version:    0.6.2
 */