package sun.plugin.liveconnect;

import com.sun.deploy.security.SecureCookiePermission;
import com.sun.deploy.trace.Trace;
import java.io.FilePermission;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.PropertyPermission;

public class SecureInvocation
{
  private static Object ConstructObject(Class paramClass, final Constructor paramConstructor, final Object[] paramArrayOfObject, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final Class val$clazz;
        private final String val$origin;
        private final boolean val$isUniversalBrowserRead;
        private final boolean val$isUniversalJavaPermission;
        private final Constructor val$constructor;
        private final Object[] val$args;

        public Object run()
          throws Exception
        {
          try
          {
            boolean bool = SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString, bool);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            return AccessController.doPrivileged(new PrivilegedConstructObjectAction(paramConstructor, paramArrayOfObject), localAccessControlContext);
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static Object CallMethod(Class paramClass, final Object paramObject, final Method paramMethod, final Object[] paramArrayOfObject, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final Class val$clazz;
        private final String val$origin;
        private final boolean val$isUniversalBrowserRead;
        private final boolean val$isUniversalJavaPermission;
        private final Method val$method;
        private final Object val$obj;
        private final Object[] val$args;

        public Object run()
          throws Exception
        {
          try
          {
            boolean bool = SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString, bool);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            return AccessController.doPrivileged(new PrivilegedCallMethodAction(paramMethod, paramObject, paramArrayOfObject), localAccessControlContext);
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static Object GetField(Class paramClass, final Object paramObject, final Field paramField, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final Class val$clazz;
        private final String val$origin;
        private final boolean val$isUniversalBrowserRead;
        private final boolean val$isUniversalJavaPermission;
        private final Field val$field;
        private final Object val$obj;

        public Object run()
          throws Exception
        {
          try
          {
            SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            return AccessController.doPrivileged(new PrivilegedGetFieldAction(paramField, paramObject), localAccessControlContext);
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static void SetField(Class paramClass, final Object paramObject1, final Field paramField, final Object paramObject2, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final Class val$clazz;
        private final String val$origin;
        private final boolean val$isUniversalBrowserRead;
        private final boolean val$isUniversalJavaPermission;
        private final Field val$field;
        private final Object val$obj;
        private final Object val$val;

        public Object run()
          throws Exception
        {
          try
          {
            SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            AccessController.doPrivileged(new PrivilegedSetFieldAction(paramField, paramObject1, paramObject2), localAccessControlContext);
            return null;
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static boolean checkLiveConnectCaller(Class paramClass, String paramString, boolean paramBoolean)
    throws OriginNotAllowedException, MalformedURLException
  {
    ProtectionDomain localProtectionDomain = paramClass.getProtectionDomain();
    CodeSource localCodeSource = localProtectionDomain.getCodeSource();
    if (localCodeSource == null)
    {
      Trace.msgLiveConnectPrintln("liveconnect.java.system");
      return true;
    }
    if (paramBoolean)
    {
      Trace.msgLiveConnectPrintln("liveconnect.UniversalBrowserRead.enabled");
      return false;
    }
    URL localURL1 = localCodeSource.getLocation();
    URL localURL2 = null;
    if (paramString != null)
      try
      {
        localURL2 = new URL(paramString);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        localMalformedURLException.printStackTrace();
        return false;
      }
    if ((localURL1 != null) && (localURL2 != null) && (localURL1.getProtocol().equalsIgnoreCase(localURL2.getProtocol())) && (localURL1.getHost().equalsIgnoreCase(localURL2.getHost())) && (localURL1.getPort() == localURL2.getPort()))
    {
      Trace.msgLiveConnectPrintln("liveconnect.same.origin");
      return false;
    }
    throw new OriginNotAllowedException("JavaScript is not from the same origin as the Java code, caller=" + localURL2 + ", callee=" + localURL1);
  }

  private static ProtectionDomain getDefaultProtectionDomain(String paramString)
    throws MalformedURLException
  {
    return getDefaultProtectionDomain(paramString, false);
  }

  private static ProtectionDomain getDefaultProtectionDomain(String paramString, boolean paramBoolean)
    throws MalformedURLException
  {
    Trace.msgLiveConnectPrintln("liveconnect.default.policy", new Object[] { paramString });
    URL localURL1 = null;
    if (paramString != null)
      try
      {
        localURL1 = new URL(paramString);
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    Policy localPolicy = Policy.getPolicy();
    CodeSource localCodeSource = new CodeSource(localURL1, (Certificate[])null);
    PermissionCollection localPermissionCollection = localPolicy.getPermissions(localCodeSource);
    localPermissionCollection.add(new PropertyPermission("http.agent", "read"));
    if ((localURL1 == null) || (localURL1.getProtocol().equals("file")))
    {
      localPermissionCollection.add(new FilePermission("<<ALL FILES>>", "read"));
    }
    else
    {
      String str1 = localURL1.getHost();
      int i = localURL1.getPort();
      if ((str1 == null) || (str1.equals("")))
        try
        {
          URL localURL2 = new URL(localURL1.getFile());
          str1 = localURL2.getHost();
          i = localURL2.getPort();
        }
        catch (Exception localException)
        {
        }
      InetAddress localInetAddress = null;
      try
      {
        localInetAddress = InetAddress.getByName(str1);
      }
      catch (UnknownHostException localUnknownHostException)
      {
        Trace.ignoredException(localUnknownHostException);
      }
      String str2 = localInetAddress != null ? localInetAddress.getHostName() : str1;
      if (i == -1)
        i = localURL1.getDefaultPort();
      if ((str2 != null) && (!str2.equals("")))
      {
        SocketPermission localSocketPermission1 = null;
        if (str2.equals("localhost"))
          localSocketPermission1 = new SocketPermission(str2 + ":" + i, "connect,accept");
        else
          localSocketPermission1 = new SocketPermission(str2, "connect,accept");
        final SocketPermission localSocketPermission2 = localSocketPermission1;
        final SecureCookiePermission localSecureCookiePermission = new SecureCookiePermission(SecureCookiePermission.getURLOriginString(localURL1));
        if (paramBoolean)
        {
          Class localClass = null;
          try
          {
            localClass = Class.forName("java.net.SocketPermission");
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            return null;
          }
          Method localMethod1 = null;
          try
          {
            localMethod1 = localClass.getDeclaredMethod("setDeny", new Class[0]);
          }
          catch (NoSuchMethodException localNoSuchMethodException)
          {
          }
          Method localMethod2 = localMethod1;
          AccessController.doPrivileged(new PrivilegedAction()
          {
            private final Method val$lookupMethod;

            public Object run()
            {
              if (this.val$lookupMethod != null)
                this.val$lookupMethod.setAccessible(true);
              return null;
            }
          });
          try
          {
            if (localMethod2 != null)
              localMethod2.invoke(localSocketPermission2, new Object[0]);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
          }
        }
        AccessController.doPrivileged(new PrivilegedAction()
        {
          private final PermissionCollection val$pc;
          private final SocketPermission val$socketPerm;
          private final SecureCookiePermission val$cookiePerm;

          public Object run()
          {
            this.val$pc.add(localSocketPermission2);
            this.val$pc.add(localSecureCookiePermission);
            return null;
          }
        });
      }
    }
    return new JavaScriptProtectionDomain(localPermissionCollection);
  }

  private static ProtectionDomain getTrustedProtectionDomain()
  {
    Trace.msgLiveConnectPrintln("liveconnect.UniversalJavaPermission.enabled");
    Permissions localPermissions = new Permissions();
    localPermissions.add(new AllPermission());
    return new JavaScriptProtectionDomain(localPermissions);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.SecureInvocation
 * JD-Core Version:    0.6.2
 */