package sun.plugin.liveconnect;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import sun.plugin.javascript.JSClassLoader;

class PrivilegedGetFieldAction
  implements PrivilegedExceptionAction
{
  Field field;
  Object obj;

  PrivilegedGetFieldAction(Field paramField, Object paramObject)
  {
    this.field = paramField;
    this.obj = paramObject;
  }

  public Object run()
    throws Exception
  {
    JSClassLoader.checkPackageAccess(this.field.getDeclaringClass());
    return this.field.get(this.obj);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.PrivilegedGetFieldAction
 * JD-Core Version:    0.6.2
 */