package sun.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;

class AppletObjectInputStream extends ObjectInputStream
{
  private ClassLoader loader;

  public AppletObjectInputStream(InputStream paramInputStream, ClassLoader paramClassLoader)
    throws IOException, StreamCorruptedException
  {
    super(paramInputStream);
    if (paramClassLoader == null)
      throw new IllegalArgumentException("appletillegalargumentexception.objectinputstream");
    this.loader = paramClassLoader;
  }

  private Class primitiveType(char paramChar)
  {
    switch (paramChar)
    {
    case 'B':
      return Byte.TYPE;
    case 'C':
      return Character.TYPE;
    case 'D':
      return Double.TYPE;
    case 'F':
      return Float.TYPE;
    case 'I':
      return Integer.TYPE;
    case 'J':
      return Long.TYPE;
    case 'S':
      return Short.TYPE;
    case 'Z':
      return Boolean.TYPE;
    case 'E':
    case 'G':
    case 'H':
    case 'K':
    case 'L':
    case 'M':
    case 'N':
    case 'O':
    case 'P':
    case 'Q':
    case 'R':
    case 'T':
    case 'U':
    case 'V':
    case 'W':
    case 'X':
    case 'Y':
    }
    return null;
  }

  protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    String str = paramObjectStreamClass.getName();
    if (str.startsWith("["))
    {
      for (int i = 1; str.charAt(i) == '['; i++);
      Class localClass;
      if (str.charAt(i) == 'L')
      {
        localClass = this.loader.loadClass(str.substring(i + 1, str.length() - 1));
      }
      else
      {
        if (str.length() != i + 1)
          throw new ClassNotFoundException(str);
        localClass = primitiveType(str.charAt(i));
      }
      int[] arrayOfInt = new int[i];
      for (int j = 0; j < i; j++)
        arrayOfInt[j] = 0;
      return Array.newInstance(localClass, arrayOfInt).getClass();
    }
    return this.loader.loadClass(str);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.AppletObjectInputStream
 * JD-Core Version:    0.6.2
 */