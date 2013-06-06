package sun.plugin.liveconnect;

import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;
import sun.plugin.javascript.JSClassLoader;

class PrivilegedConstructObjectAction
  implements PrivilegedExceptionAction
{
  Constructor constructor;
  Object[] args;

  PrivilegedConstructObjectAction(Constructor paramConstructor, Object[] paramArrayOfObject)
  {
    this.constructor = paramConstructor;
    this.args = paramArrayOfObject;
    if (this.args == null)
      this.args = new Object[0];
  }

  public Object run()
    throws Exception
  {
    JSClassLoader.checkPackageAccess(this.constructor.getDeclaringClass());
    return this.constructor.newInstance(this.args);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.PrivilegedConstructObjectAction
 * JD-Core Version:    0.6.2
 */