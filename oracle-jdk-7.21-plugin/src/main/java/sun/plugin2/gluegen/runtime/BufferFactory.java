package sun.plugin2.gluegen.runtime;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class BufferFactory
{
  public static final int SIZEOF_BYTE = 1;
  public static final int SIZEOF_SHORT = 2;
  public static final int SIZEOF_CHAR = 2;
  public static final int SIZEOF_INT = 4;
  public static final int SIZEOF_FLOAT = 4;
  public static final int SIZEOF_LONG = 8;
  public static final int SIZEOF_DOUBLE = 8;

  public static ByteBuffer newDirectByteBuffer(int paramInt)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(paramInt);
    localByteBuffer.order(ByteOrder.nativeOrder());
    return localByteBuffer;
  }

  public static boolean isDirect(Buffer paramBuffer)
  {
    if (paramBuffer == null)
      return true;
    if ((paramBuffer instanceof ByteBuffer))
      return ((ByteBuffer)paramBuffer).isDirect();
    if ((paramBuffer instanceof FloatBuffer))
      return ((FloatBuffer)paramBuffer).isDirect();
    if ((paramBuffer instanceof DoubleBuffer))
      return ((DoubleBuffer)paramBuffer).isDirect();
    if ((paramBuffer instanceof CharBuffer))
      return ((CharBuffer)paramBuffer).isDirect();
    if ((paramBuffer instanceof ShortBuffer))
      return ((ShortBuffer)paramBuffer).isDirect();
    if ((paramBuffer instanceof IntBuffer))
      return ((IntBuffer)paramBuffer).isDirect();
    if ((paramBuffer instanceof LongBuffer))
      return ((LongBuffer)paramBuffer).isDirect();
    throw new RuntimeException("Unexpected buffer type " + paramBuffer.getClass().getName());
  }

  public static int getDirectBufferByteOffset(Buffer paramBuffer)
  {
    if (paramBuffer == null)
      return 0;
    if ((paramBuffer instanceof ByteBuffer))
      return paramBuffer.position();
    if ((paramBuffer instanceof FloatBuffer))
      return paramBuffer.position() * 4;
    if ((paramBuffer instanceof IntBuffer))
      return paramBuffer.position() * 4;
    if ((paramBuffer instanceof ShortBuffer))
      return paramBuffer.position() * 2;
    if ((paramBuffer instanceof DoubleBuffer))
      return paramBuffer.position() * 8;
    if ((paramBuffer instanceof LongBuffer))
      return paramBuffer.position() * 8;
    if ((paramBuffer instanceof CharBuffer))
      return paramBuffer.position() * 2;
    throw new RuntimeException("Disallowed array backing store type in buffer " + paramBuffer.getClass().getName());
  }

  public static Object getArray(Buffer paramBuffer)
  {
    if (paramBuffer == null)
      return null;
    if ((paramBuffer instanceof ByteBuffer))
      return ((ByteBuffer)paramBuffer).array();
    if ((paramBuffer instanceof FloatBuffer))
      return ((FloatBuffer)paramBuffer).array();
    if ((paramBuffer instanceof IntBuffer))
      return ((IntBuffer)paramBuffer).array();
    if ((paramBuffer instanceof ShortBuffer))
      return ((ShortBuffer)paramBuffer).array();
    if ((paramBuffer instanceof DoubleBuffer))
      return ((DoubleBuffer)paramBuffer).array();
    if ((paramBuffer instanceof LongBuffer))
      return ((LongBuffer)paramBuffer).array();
    if ((paramBuffer instanceof CharBuffer))
      return ((CharBuffer)paramBuffer).array();
    throw new RuntimeException("Disallowed array backing store type in buffer " + paramBuffer.getClass().getName());
  }

  public static int getIndirectBufferByteOffset(Buffer paramBuffer)
  {
    if (paramBuffer == null)
      return 0;
    int i = paramBuffer.position();
    if ((paramBuffer instanceof ByteBuffer))
      return ((ByteBuffer)paramBuffer).arrayOffset() + i;
    if ((paramBuffer instanceof FloatBuffer))
      return 4 * (((FloatBuffer)paramBuffer).arrayOffset() + i);
    if ((paramBuffer instanceof IntBuffer))
      return 4 * (((IntBuffer)paramBuffer).arrayOffset() + i);
    if ((paramBuffer instanceof ShortBuffer))
      return 2 * (((ShortBuffer)paramBuffer).arrayOffset() + i);
    if ((paramBuffer instanceof DoubleBuffer))
      return 8 * (((DoubleBuffer)paramBuffer).arrayOffset() + i);
    if ((paramBuffer instanceof LongBuffer))
      return 8 * (((LongBuffer)paramBuffer).arrayOffset() + i);
    if ((paramBuffer instanceof CharBuffer))
      return 2 * (((CharBuffer)paramBuffer).arrayOffset() + i);
    throw new RuntimeException("Unknown buffer type " + paramBuffer.getClass().getName());
  }

  public static void rangeCheck(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null)
      return;
    if (paramArrayOfByte.length < paramInt1 + paramInt2)
      throw new ArrayIndexOutOfBoundsException("Required " + paramInt2 + " elements in array, only had " + (paramArrayOfByte.length - paramInt1));
  }

  public static void rangeCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null)
      return;
    if (paramArrayOfChar.length < paramInt1 + paramInt2)
      throw new ArrayIndexOutOfBoundsException("Required " + paramInt2 + " elements in array, only had " + (paramArrayOfChar.length - paramInt1));
  }

  public static void rangeCheck(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    if (paramArrayOfShort == null)
      return;
    if (paramArrayOfShort.length < paramInt1 + paramInt2)
      throw new ArrayIndexOutOfBoundsException("Required " + paramInt2 + " elements in array, only had " + (paramArrayOfShort.length - paramInt1));
  }

  public static void rangeCheck(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramArrayOfInt == null)
      return;
    if (paramArrayOfInt.length < paramInt1 + paramInt2)
      throw new ArrayIndexOutOfBoundsException("Required " + paramInt2 + " elements in array, only had " + (paramArrayOfInt.length - paramInt1));
  }

  public static void rangeCheck(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    if (paramArrayOfLong == null)
      return;
    if (paramArrayOfLong.length < paramInt1 + paramInt2)
      throw new ArrayIndexOutOfBoundsException("Required " + paramInt2 + " elements in array, only had " + (paramArrayOfLong.length - paramInt1));
  }

  public static void rangeCheck(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramArrayOfFloat == null)
      return;
    if (paramArrayOfFloat.length < paramInt1 + paramInt2)
      throw new ArrayIndexOutOfBoundsException("Required " + paramInt2 + " elements in array, only had " + (paramArrayOfFloat.length - paramInt1));
  }

  public static void rangeCheck(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    if (paramArrayOfDouble == null)
      return;
    if (paramArrayOfDouble.length < paramInt1 + paramInt2)
      throw new ArrayIndexOutOfBoundsException("Required " + paramInt2 + " elements in array, only had " + (paramArrayOfDouble.length - paramInt1));
  }

  public static void rangeCheck(Buffer paramBuffer, int paramInt)
  {
    if (paramBuffer == null)
      return;
    if (paramBuffer.remaining() < paramInt)
      throw new IndexOutOfBoundsException("Required " + paramInt + " remaining elements in buffer, only had " + paramBuffer.remaining());
  }

  public static void rangeCheckBytes(Buffer paramBuffer, int paramInt)
  {
    if (paramBuffer == null)
      return;
    int i = paramBuffer.remaining();
    int j = 0;
    if ((paramBuffer instanceof ByteBuffer))
      j = i;
    else if ((paramBuffer instanceof FloatBuffer))
      j = i * 4;
    else if ((paramBuffer instanceof IntBuffer))
      j = i * 4;
    else if ((paramBuffer instanceof ShortBuffer))
      j = i * 2;
    else if ((paramBuffer instanceof DoubleBuffer))
      j = i * 8;
    else if ((paramBuffer instanceof LongBuffer))
      j = i * 8;
    else if ((paramBuffer instanceof CharBuffer))
      j = i * 2;
    if (j < paramInt)
      throw new IndexOutOfBoundsException("Required " + paramInt + " remaining bytes in buffer, only had " + j);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.gluegen.runtime.BufferFactory
 * JD-Core Version:    0.6.2
 */