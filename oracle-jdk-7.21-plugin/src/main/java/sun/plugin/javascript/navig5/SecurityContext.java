package sun.plugin.javascript.navig5;

import com.sun.deploy.trace.Trace;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import sun.applet.AppletClassLoader;
import sun.plugin.security.ActivatorSecurityManager;

class SecurityContext
{
  private ProtectionDomain domain;
  private AccessControlContext ctx;

  SecurityContext(ProtectionDomain paramProtectionDomain, AccessControlContext paramAccessControlContext)
  {
    this.domain = paramProtectionDomain;
    this.ctx = paramAccessControlContext;
  }

  String getURL()
  {
    if (this.domain != null)
    {
      CodeSource localCodeSource = this.domain.getCodeSource();
      if (localCodeSource != null)
      {
        URL localURL = localCodeSource.getLocation();
        if (localURL != null)
        {
          StringBuffer localStringBuffer = new StringBuffer();
          String str1 = localURL.getProtocol();
          String str2 = localURL.getHost();
          int i = localURL.getPort();
          localStringBuffer.append(str1);
          localStringBuffer.append("://");
          localStringBuffer.append(str2);
          if (i != -1)
            localStringBuffer.append(":" + i);
          return localStringBuffer.toString();
        }
      }
    }
    return "file://";
  }

  byte[][] getCertChain()
  {
    return (byte[][])null;
  }

  int[] getCertLength()
  {
    return null;
  }

  int getNumOfCert()
  {
    return 0;
  }

  AccessControlContext getAccessControlContext()
  {
    return this.ctx;
  }

  static SecurityContext getCurrentSecurityContext()
  {
    AccessControlContext localAccessControlContext = AccessController.getContext();
    try
    {
      return (SecurityContext)AccessController.doPrivileged(new PrivilegedBlockAction(localAccessControlContext));
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Trace.securityPrintException(localPrivilegedActionException);
    }
    return new SecurityContext(null, localAccessControlContext);
  }

  static class PrivilegedBlockAction
    implements PrivilegedExceptionAction
  {
    AccessControlContext ctx;

    PrivilegedBlockAction(AccessControlContext paramAccessControlContext)
    {
      this.ctx = paramAccessControlContext;
    }

    public Object run()
      throws Exception
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if ((localSecurityManager != null) && ((localSecurityManager instanceof ActivatorSecurityManager)))
      {
        ActivatorSecurityManager localActivatorSecurityManager = (ActivatorSecurityManager)localSecurityManager;
        Class[] arrayOfClass = localActivatorSecurityManager.getExecutionStackContext();
        for (int i = 0; i < arrayOfClass.length; i++)
        {
          Class localClass = arrayOfClass[i];
          ClassLoader localClassLoader = localClass.getClassLoader();
          if (((localClassLoader instanceof URLClassLoader)) || ((localClassLoader instanceof AppletClassLoader)) || (AppletClassLoader.class.isAssignableFrom(localClass)))
            return new SecurityContext(localClass.getProtectionDomain(), this.ctx);
        }
      }
      return new SecurityContext(null, this.ctx);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig5.SecurityContext
 * JD-Core Version:    0.6.2
 */