package sun.plugin2.gluegen.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class StructAccessor
{
  private ByteBuffer bb;
  private CharBuffer cb;
  private DoubleBuffer db;
  private FloatBuffer fb;
  private IntBuffer ib;
  private LongBuffer lb;
  private ShortBuffer sb;

  public StructAccessor(ByteBuffer paramByteBuffer)
  {
    this.bb = paramByteBuffer.order(ByteOrder.nativeOrder());
  }

  public ByteBuffer getBuffer()
  {
    return this.bb;
  }

  public ByteBuffer slice(int paramInt1, int paramInt2)
  {
    this.bb.position(paramInt1);
    this.bb.limit(paramInt1 + paramInt2);
    ByteBuffer localByteBuffer = this.bb.slice();
    this.bb.position(0);
    this.bb.limit(this.bb.capacity());
    return localByteBuffer;
  }

  public byte getByteAt(int paramInt)
  {
    return this.bb.get(paramInt);
  }

  public void setByteAt(int paramInt, byte paramByte)
  {
    this.bb.put(paramInt, paramByte);
  }

  public char getCharAt(int paramInt)
  {
    return charBuffer().get(paramInt);
  }

  public void setCharAt(int paramInt, char paramChar)
  {
    charBuffer().put(paramInt, paramChar);
  }

  public double getDoubleAt(int paramInt)
  {
    return doubleBuffer().get(paramInt);
  }

  public void setDoubleAt(int paramInt, double paramDouble)
  {
    doubleBuffer().put(paramInt, paramDouble);
  }

  public float getFloatAt(int paramInt)
  {
    return floatBuffer().get(paramInt);
  }

  public void setFloatAt(int paramInt, float paramFloat)
  {
    floatBuffer().put(paramInt, paramFloat);
  }

  public int getIntAt(int paramInt)
  {
    return intBuffer().get(paramInt);
  }

  public void setIntAt(int paramInt1, int paramInt2)
  {
    intBuffer().put(paramInt1, paramInt2);
  }

  public long getLongAt(int paramInt)
  {
    return longBuffer().get(paramInt);
  }

  public void setLongAt(int paramInt, long paramLong)
  {
    longBuffer().put(paramInt, paramLong);
  }

  public short getShortAt(int paramInt)
  {
    return shortBuffer().get(paramInt);
  }

  public void setShortAt(int paramInt, short paramShort)
  {
    shortBuffer().put(paramInt, paramShort);
  }

  private CharBuffer charBuffer()
  {
    if (this.cb == null)
      this.cb = this.bb.asCharBuffer();
    return this.cb;
  }

  private DoubleBuffer doubleBuffer()
  {
    if (this.db == null)
      this.db = this.bb.asDoubleBuffer();
    return this.db;
  }

  private FloatBuffer floatBuffer()
  {
    if (this.fb == null)
      this.fb = this.bb.asFloatBuffer();
    return this.fb;
  }

  private IntBuffer intBuffer()
  {
    if (this.ib == null)
      this.ib = this.bb.asIntBuffer();
    return this.ib;
  }

  private LongBuffer longBuffer()
  {
    if (this.lb == null)
      this.lb = this.bb.asLongBuffer();
    return this.lb;
  }

  private ShortBuffer shortBuffer()
  {
    if (this.sb == null)
      this.sb = this.bb.asShortBuffer();
    return this.sb;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.gluegen.runtime.StructAccessor
 * JD-Core Version:    0.6.2
 */