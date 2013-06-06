package sun.plugin2.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

public class PojoUtil
{
  private final StringBuffer sb = new StringBuffer();
  private final ArrayList path = new ArrayList();

  public static String toJson(Object paramObject)
  {
    PojoUtil localPojoUtil = new PojoUtil();
    localPojoUtil.toJson(paramObject, 0);
    return localPojoUtil.sb.toString();
  }

  private void indent(int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
      this.sb.append("  ");
  }

  private void toJson(Object paramObject, int paramInt)
  {
    if (this.path.contains(paramObject))
    {
      this.sb.append("{/*loop->").append(paramObject.getClass().getName()).append("@").append(paramObject.hashCode()).append("*/}");
      return;
    }
    this.path.add(paramObject);
    if (null == paramObject)
    {
      this.sb.append("null");
    }
    else if (((paramObject instanceof String)) || ((paramObject instanceof Character)))
    {
      this.sb.append('"').append(paramObject).append('"');
    }
    else if ((paramObject instanceof Boolean))
    {
      this.sb.append(((Boolean)paramObject).booleanValue() ? "true" : "false");
    }
    else if (((paramObject instanceof Byte)) || ((paramObject instanceof Short)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Long)) || ((paramObject instanceof Float)) || ((paramObject instanceof Double)))
    {
      this.sb.append(paramObject);
    }
    else if (paramObject.getClass().isArray())
    {
      int i = Array.getLength(paramObject);
      if (i == 0)
      {
        this.sb.append("[]");
      }
      else
      {
        this.sb.append("[\n");
        for (int j = 0; j < i; j++)
        {
          if (j != 0)
            this.sb.append(",\n");
          indent(paramInt + 1);
          toJson(Array.get(paramObject, j), paramInt + 1);
        }
        this.sb.append("\n");
        indent(paramInt);
        this.sb.append(']');
      }
    }
    else
    {
      pojo2json(paramObject, paramInt, paramObject.getClass());
    }
    this.path.remove(this.path.size() - 1);
  }

  private void pojo2json(final Object paramObject, final int paramInt, final Class paramClass)
  {
    if (paramInt > 10)
    {
      this.sb.append("{/*...*/}");
      return;
    }
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final Class val$cls;
      private final int val$depth;
      private final Object val$obj;

      public Object run()
      {
        Field[] arrayOfField = paramClass.getDeclaredFields();
        boolean bool1 = true;
        PojoUtil.this.sb.append("{");
        for (int i = 0; i < arrayOfField.length; i++)
        {
          Field localField = arrayOfField[i];
          int j = 24;
          if (j != (localField.getModifiers() & j))
          {
            bool1 = false;
            boolean bool2 = localField.isAccessible();
            if (!bool2)
              localField.setAccessible(true);
            if (i != 0)
              PojoUtil.this.sb.append(",\n");
            else
              PojoUtil.this.sb.append("\n");
            PojoUtil.this.indent(paramInt + 1);
            PojoUtil.this.sb.append('"').append(arrayOfField[i].getName()).append("\": ");
            try
            {
              PojoUtil.this.toJson(arrayOfField[i].get(paramObject), paramInt + 1);
            }
            catch (IllegalArgumentException localIllegalArgumentException)
            {
              localIllegalArgumentException.printStackTrace();
            }
            catch (IllegalAccessException localIllegalAccessException)
            {
              localIllegalAccessException.printStackTrace();
            }
            localField.setAccessible(bool2);
          }
        }
        return Boolean.valueOf(bool1);
      }
    })).booleanValue();
    Class localClass = paramClass.getSuperclass();
    if ((localClass != null) && (localClass != Object.class))
    {
      if (!bool)
        this.sb.append(",\n");
      else
        this.sb.append("\n");
      bool = false;
      indent(paramInt + 1);
      this.sb.append('"').append(localClass.getName()).append("\": ");
      pojo2json(paramObject, paramInt + 1, localClass);
    }
    if (!bool)
    {
      this.sb.append("\n");
      indent(paramInt);
    }
    this.sb.append("}");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.util.PojoUtil
 * JD-Core Version:    0.6.2
 */