package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.util.Collection;

public abstract class CertStoreSpi
{
  public CertStoreSpi(CertStoreParameters paramCertStoreParameters)
    throws InvalidAlgorithmParameterException
  {
  }

  public abstract Collection<? extends Certificate> engineGetCertificates(CertSelector paramCertSelector)
    throws CertStoreException;

  public abstract Collection<? extends CRL> engineGetCRLs(CRLSelector paramCRLSelector)
    throws CertStoreException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.CertStoreSpi
 * JD-Core Version:    0.6.2
 */