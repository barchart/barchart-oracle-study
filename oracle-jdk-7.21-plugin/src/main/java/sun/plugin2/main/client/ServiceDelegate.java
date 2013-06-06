package sun.plugin2.main.client;

import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import java.security.KeyStore;
import java.security.SecureRandom;
import sun.plugin2.util.SystemUtil;

public class ServiceDelegate
{
  private static ServiceDelegate soleInstance;

  public static void initialize(int paramInt)
    throws IllegalArgumentException
  {
    try
    {
      switch (paramInt)
      {
      case 1:
        soleInstance = new ServiceDelegate();
        break;
      case 2:
        soleInstance = (ServiceDelegate)Class.forName("sun.plugin2.main.client.WIExplorerServiceDelegate").newInstance();
        break;
      case 3:
        if (SystemUtil.getOSType() == 1)
          soleInstance = (ServiceDelegate)Class.forName("sun.plugin2.main.client.WMozillaServiceDelegate").newInstance();
        else if (SystemUtil.getOSType() == 3)
          soleInstance = (ServiceDelegate)Class.forName("sun.plugin2.main.client.MacOSXMozillaServiceDelegate").newInstance();
        else
          soleInstance = (ServiceDelegate)Class.forName("sun.plugin2.main.client.MMozillaServiceDelegate").newInstance();
        break;
      default:
        throw new IllegalArgumentException("Unknown browser type " + paramInt);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new RuntimeException(localClassNotFoundException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
  }

  public static ServiceDelegate get()
  {
    if (soleInstance == null)
      throw new RuntimeException("Must call ServiceDelegate.initialize() first");
    return soleInstance;
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    return null;
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    return null;
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return null;
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    return null;
  }

  public CredentialManager getCredentialManager()
  {
    return null;
  }

  public SecureRandom getSecureRandom()
  {
    return new SecureRandom();
  }

  public boolean isIExplorer()
  {
    return false;
  }

  public boolean isNetscape()
  {
    return false;
  }

  public void AllowSetForegroundWindow(long paramLong)
  {
  }

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return null;
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return null;
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return null;
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.ServiceDelegate
 * JD-Core Version:    0.6.2
 */