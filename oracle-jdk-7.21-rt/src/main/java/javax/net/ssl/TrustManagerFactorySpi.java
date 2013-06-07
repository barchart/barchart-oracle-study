package javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;

public abstract class TrustManagerFactorySpi
{
  protected abstract void engineInit(KeyStore paramKeyStore)
    throws KeyStoreException;

  protected abstract void engineInit(ManagerFactoryParameters paramManagerFactoryParameters)
    throws InvalidAlgorithmParameterException;

  protected abstract TrustManager[] engineGetTrustManagers();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.net.ssl.TrustManagerFactorySpi
 * JD-Core Version:    0.6.2
 */