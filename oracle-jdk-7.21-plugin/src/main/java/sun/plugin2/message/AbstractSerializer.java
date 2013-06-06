package sun.plugin2.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractSerializer
  implements Serializer
{
  public void writeBoolean(boolean paramBoolean)
    throws IOException
  {
    writeByte((byte)(paramBoolean ? 1 : 0));
  }

  public abstract void writeByte(byte paramByte)
    throws IOException;

  public void writeShort(short paramShort)
    throws IOException
  {
    writeByte((byte)(paramShort >>> 8));
    writeByte((byte)paramShort);
  }

  public void writeChar(char paramChar)
    throws IOException
  {
    writeShort((short)paramChar);
  }

  public void writeInt(int paramInt)
    throws IOException
  {
    writeByte((byte)(paramInt >>> 24));
    writeByte((byte)(paramInt >>> 16));
    writeByte((byte)(paramInt >>> 8));
    writeByte((byte)paramInt);
  }

  public void writeLong(long paramLong)
    throws IOException
  {
    writeByte((byte)(int)(paramLong >>> 56));
    writeByte((byte)(int)(paramLong >>> 48));
    writeByte((byte)(int)(paramLong >>> 40));
    writeByte((byte)(int)(paramLong >>> 32));
    writeByte((byte)(int)(paramLong >>> 24));
    writeByte((byte)(int)(paramLong >>> 16));
    writeByte((byte)(int)(paramLong >>> 8));
    writeByte((byte)(int)paramLong);
  }

  public void writeFloat(float paramFloat)
    throws IOException
  {
    writeInt(Float.floatToRawIntBits(paramFloat));
  }

  public void writeDouble(double paramDouble)
    throws IOException
  {
    writeLong(Double.doubleToRawLongBits(paramDouble));
  }

  public void writeByteArray(byte[] paramArrayOfByte)
    throws IOException
  {
    writeByteArray(paramArrayOfByte, 0, paramArrayOfByte == null ? 0 : paramArrayOfByte.length);
  }

  public void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null)
    {
      writeBoolean(false);
      return;
    }
    writeBoolean(true);
    writeInt(paramInt2);
    for (int i = 0; i < paramInt2; i++)
      writeByte(paramArrayOfByte[(paramInt1 + i)]);
  }

  public void writeCharArray(char[] paramArrayOfChar)
    throws IOException
  {
    writeCharArray(paramArrayOfChar, 0, paramArrayOfChar == null ? 0 : paramArrayOfChar.length);
  }

  public void writeCharArray(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfChar == null)
    {
      writeBoolean(false);
      return;
    }
    writeBoolean(true);
    writeInt(paramInt2);
    for (int i = 0; i < paramInt2; i++)
      writeChar(paramArrayOfChar[(paramInt1 + i)]);
  }

  public void writeIntegerArray(Integer[] paramArrayOfInteger)
    throws IOException
  {
    if ((paramArrayOfInteger == null) || (paramArrayOfInteger.length == 0))
    {
      writeBoolean(false);
      return;
    }
    writeBoolean(true);
    writeInt(paramArrayOfInteger.length);
    for (int i = 0; i < paramArrayOfInteger.length; i++)
      writeInt(paramArrayOfInteger[i].intValue());
  }

  public void writeUTF(String paramString)
    throws IOException
  {
    if (paramString == null)
    {
      writeBoolean(false);
      return;
    }
    writeBoolean(true);
    int i = paramString.length();
    writeInt(i);
    for (int j = 0; j < i; j++)
      writeChar(paramString.charAt(j));
  }

  public void writeUTFArray(String[] paramArrayOfString)
    throws IOException
  {
    if (paramArrayOfString == null)
    {
      writeBoolean(false);
      return;
    }
    writeBoolean(true);
    writeInt(paramArrayOfString.length);
    for (int i = 0; i < paramArrayOfString.length; i++)
      writeUTF(paramArrayOfString[i]);
  }

  public void writeConversation(Conversation paramConversation)
    throws IOException
  {
    if (paramConversation == null)
    {
      writeBoolean(false);
      return;
    }
    writeBoolean(true);
    paramConversation.writeFields(this);
  }

  public abstract void flush()
    throws IOException;

  public boolean readBoolean()
    throws IOException
  {
    return readByte() != 0;
  }

  public abstract byte readByte()
    throws IOException;

  public short readShort()
    throws IOException
  {
    short s = (short)((readByte() & 0xFF) << 8 | readByte() & 0xFF);
    return s;
  }

  public char readChar()
    throws IOException
  {
    return (char)readShort();
  }

  public int readInt()
    throws IOException
  {
    int i = readByte() & 0xFF;
    i = i << 8 | readByte() & 0xFF;
    i = i << 8 | readByte() & 0xFF;
    i = i << 8 | readByte() & 0xFF;
    return i;
  }

  public long readLong()
    throws IOException
  {
    long l = readByte() & 0xFF;
    l = l << 8 | readByte() & 0xFF;
    l = l << 8 | readByte() & 0xFF;
    l = l << 8 | readByte() & 0xFF;
    l = l << 8 | readByte() & 0xFF;
    l = l << 8 | readByte() & 0xFF;
    l = l << 8 | readByte() & 0xFF;
    l = l << 8 | readByte() & 0xFF;
    return l;
  }

  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }

  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(readLong());
  }

  public ByteBuffer readByteBuffer()
    throws IOException
  {
    if (!readBoolean())
      return null;
    int i = readInt();
    ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(i);
    for (int j = 0; j < i; j++)
      localByteBuffer.put(readByte());
    localByteBuffer.rewind();
    return localByteBuffer;
  }

  public byte[] readByteArray()
    throws IOException
  {
    if (!readBoolean())
      return null;
    int i = readInt();
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    for (int k = 0; k < i; k++)
      arrayOfByte[k] = readByte();
    return arrayOfByte;
  }

  public char[] readCharArray()
    throws IOException
  {
    if (!readBoolean())
      return null;
    int i = readInt();
    char[] arrayOfChar = new char[i];
    for (int j = 0; j < i; j++)
      arrayOfChar[j] = readChar();
    return arrayOfChar;
  }

  public Integer[] readIntegerArray()
    throws IOException
  {
    if (!readBoolean())
      return null;
    int i = readInt();
    Integer[] arrayOfInteger = new Integer[i];
    for (int j = 0; j < i; j++)
      arrayOfInteger[j] = new Integer(readInt());
    return arrayOfInteger;
  }

  public String readUTF()
    throws IOException
  {
    if (!readBoolean())
      return null;
    int i = readInt();
    StringBuffer localStringBuffer = new StringBuffer(i);
    for (int j = 0; j < i; j++)
      localStringBuffer.append(readChar());
    return localStringBuffer.toString();
  }

  public String[] readUTFArray()
    throws IOException
  {
    if (!readBoolean())
      return null;
    int i = readInt();
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++)
      arrayOfString[j] = readUTF();
    return arrayOfString;
  }

  public Conversation readConversation()
    throws IOException
  {
    if (!readBoolean())
      return null;
    Conversation localConversation = new Conversation();
    localConversation.readFields(this);
    return localConversation;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.AbstractSerializer
 * JD-Core Version:    0.6.2
 */