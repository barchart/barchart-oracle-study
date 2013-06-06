package sun.plugin2.liveconnect;

import java.io.IOException;
import sun.plugin2.message.Serializer;

public final class ArgumentHelper
{
  private static final int NULL_TAG = 0;
  private static final int STRING_TAG = 1;
  private static final int BOOLEAN_TAG = 2;
  private static final int BYTE_TAG = 3;
  private static final int CHARACTER_TAG = 4;
  private static final int SHORT_TAG = 5;
  private static final int INTEGER_TAG = 6;
  private static final int LONG_TAG = 7;
  private static final int FLOAT_TAG = 8;
  private static final int DOUBLE_TAG = 9;
  private static final int BROWSER_SIDE_OBJECT_TAG = 10;
  private static final int REMOTE_JAVA_OBJECT_TAG = 11;

  public static boolean isPrimitiveOrString(Object paramObject)
  {
    if ((paramObject instanceof String))
      return true;
    return isPrimitive(paramObject);
  }

  public static boolean isPrimitive(Object paramObject)
  {
    return ((paramObject instanceof Boolean)) || ((paramObject instanceof Byte)) || ((paramObject instanceof Character)) || ((paramObject instanceof Short)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Long)) || ((paramObject instanceof Float)) || ((paramObject instanceof Double));
  }

  public static void validate(Object[] paramArrayOfObject)
    throws IllegalArgumentException
  {
    if (paramArrayOfObject != null)
      for (int i = 0; i < paramArrayOfObject.length; i++)
        validate(paramArrayOfObject[i]);
  }

  public static void writeObject(Serializer paramSerializer, Object paramObject)
    throws IOException
  {
    if (paramObject == null)
    {
      paramSerializer.writeByte((byte)0);
      return;
    }
    if ((paramObject instanceof String))
    {
      paramSerializer.writeByte((byte)1);
      paramSerializer.writeUTF((String)paramObject);
    }
    else if ((paramObject instanceof Boolean))
    {
      paramSerializer.writeByte((byte)2);
      paramSerializer.writeBoolean(((Boolean)paramObject).booleanValue());
    }
    else if ((paramObject instanceof Byte))
    {
      paramSerializer.writeByte((byte)3);
      paramSerializer.writeByte(((Byte)paramObject).byteValue());
    }
    else if ((paramObject instanceof Character))
    {
      paramSerializer.writeByte((byte)4);
      paramSerializer.writeChar(((Character)paramObject).charValue());
    }
    else if ((paramObject instanceof Short))
    {
      paramSerializer.writeByte((byte)5);
      paramSerializer.writeShort(((Short)paramObject).shortValue());
    }
    else if ((paramObject instanceof Integer))
    {
      paramSerializer.writeByte((byte)6);
      paramSerializer.writeInt(((Integer)paramObject).intValue());
    }
    else if ((paramObject instanceof Long))
    {
      paramSerializer.writeByte((byte)7);
      paramSerializer.writeLong(((Long)paramObject).longValue());
    }
    else if ((paramObject instanceof Float))
    {
      paramSerializer.writeByte((byte)8);
      paramSerializer.writeFloat(((Float)paramObject).floatValue());
    }
    else if ((paramObject instanceof Double))
    {
      paramSerializer.writeByte((byte)9);
      paramSerializer.writeDouble(((Double)paramObject).doubleValue());
    }
    else if ((paramObject instanceof BrowserSideObject))
    {
      paramSerializer.writeByte((byte)10);
      paramSerializer.writeLong(((BrowserSideObject)paramObject).getNativeObjectReference());
    }
    else if ((paramObject instanceof RemoteJavaObject))
    {
      paramSerializer.writeByte((byte)11);
      RemoteJavaObject.write(paramSerializer, (RemoteJavaObject)paramObject);
    }
    else
    {
      throw new RuntimeException("Can't serialize objects of type " + paramObject.getClass().getName());
    }
  }

  public static Object readObject(Serializer paramSerializer)
    throws IOException
  {
    int i = paramSerializer.readByte() & 0xFF;
    switch (i)
    {
    case 0:
      return null;
    case 1:
      return paramSerializer.readUTF();
    case 2:
      return paramSerializer.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
    case 3:
      return new Byte(paramSerializer.readByte());
    case 4:
      return new Character(paramSerializer.readChar());
    case 5:
      return new Short(paramSerializer.readShort());
    case 6:
      return new Integer(paramSerializer.readInt());
    case 7:
      return new Long(paramSerializer.readLong());
    case 8:
      return new Float(paramSerializer.readFloat());
    case 9:
      return new Double(paramSerializer.readDouble());
    case 10:
      return new BrowserSideObject(paramSerializer.readLong());
    case 11:
      return RemoteJavaObject.read(paramSerializer);
    }
    throw new RuntimeException("Unexpected object tag " + i);
  }

  public static void writeObjectArray(Serializer paramSerializer, Object[] paramArrayOfObject)
    throws IOException
  {
    if (paramArrayOfObject == null)
    {
      paramSerializer.writeBoolean(false);
      return;
    }
    paramSerializer.writeBoolean(true);
    paramSerializer.writeInt(paramArrayOfObject.length);
    for (int i = 0; i < paramArrayOfObject.length; i++)
      writeObject(paramSerializer, paramArrayOfObject[i]);
  }

  public static Object[] readObjectArray(Serializer paramSerializer)
    throws IOException
  {
    if (!paramSerializer.readBoolean())
      return null;
    int i = paramSerializer.readInt();
    Object[] arrayOfObject = new Object[i];
    for (int j = 0; j < i; j++)
      arrayOfObject[j] = readObject(paramSerializer);
    return arrayOfObject;
  }

  private static void validate(Object paramObject)
    throws IllegalArgumentException
  {
    if (paramObject == null)
      return;
    if (isPrimitiveOrString(paramObject))
      return;
    if ((paramObject instanceof BrowserSideObject))
      return;
    if ((paramObject instanceof RemoteJavaObject))
      return;
    throw new IllegalArgumentException("Can't pass instances of class " + paramObject.getClass().getName() + " between JVMs");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.liveconnect.ArgumentHelper
 * JD-Core Version:    0.6.2
 */