package sun.plugin.liveconnect;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import sun.plugin.javascript.JSClassLoader;

class PrivilegedSetFieldAction
  implements PrivilegedExceptionAction
{
  Field field;
  Object obj;
  Object val;

  PrivilegedSetFieldAction(Field paramField, Object paramObject1, Object paramObject2)
  {
    this.field = paramField;
    this.obj = paramObject1;
    this.val = paramObject2;
  }

  public Object run()
    throws Exception
  {
    JSClassLoader.checkPackageAccess(this.field.getDeclaringClass());
    this.field.set(this.obj, this.val);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.PrivilegedSetFieldAction
 * JD-Core Version:    0.6.2
 */