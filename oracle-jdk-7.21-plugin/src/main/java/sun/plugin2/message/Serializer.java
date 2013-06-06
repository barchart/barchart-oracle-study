package sun.plugin2.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface Serializer
{
  public abstract void writeBoolean(boolean paramBoolean)
    throws IOException;

  public abstract void writeByte(byte paramByte)
    throws IOException;

  public abstract void writeShort(short paramShort)
    throws IOException;

  public abstract void writeChar(char paramChar)
    throws IOException;

  public abstract void writeInt(int paramInt)
    throws IOException;

  public abstract void writeLong(long paramLong)
    throws IOException;

  public abstract void writeFloat(float paramFloat)
    throws IOException;

  public abstract void writeDouble(double paramDouble)
    throws IOException;

  public abstract void writeByteArray(byte[] paramArrayOfByte)
    throws IOException;

  public abstract void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;

  public abstract void writeCharArray(char[] paramArrayOfChar)
    throws IOException;

  public abstract void writeCharArray(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException;

  public abstract void writeIntegerArray(Integer[] paramArrayOfInteger)
    throws IOException;

  public abstract void writeUTF(String paramString)
    throws IOException;

  public abstract void writeUTFArray(String[] paramArrayOfString)
    throws IOException;

  public abstract void flush()
    throws IOException;

  public abstract boolean readBoolean()
    throws IOException;

  public abstract byte readByte()
    throws IOException;

  public abstract short readShort()
    throws IOException;

  public abstract char readChar()
    throws IOException;

  public abstract int readInt()
    throws IOException;

  public abstract long readLong()
    throws IOException;

  public abstract float readFloat()
    throws IOException;

  public abstract double readDouble()
    throws IOException;

  public abstract ByteBuffer readByteBuffer()
    throws IOException;

  public abstract byte[] readByteArray()
    throws IOException;

  public abstract char[] readCharArray()
    throws IOException;

  public abstract Integer[] readIntegerArray()
    throws IOException;

  public abstract String readUTF()
    throws IOException;

  public abstract String[] readUTFArray()
    throws IOException;

  public abstract Conversation readConversation()
    throws IOException;

  public abstract void writeConversation(Conversation paramConversation)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.Serializer
 * JD-Core Version:    0.6.2
 */