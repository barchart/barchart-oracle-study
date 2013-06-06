package sun.plugin2.liveconnect;

import java.io.IOException;
import sun.plugin2.message.Serializer;

public final class BrowserSideObject
{
  private long nativeObjectReference;

  public BrowserSideObject(long paramLong)
  {
    this.nativeObjectReference = paramLong;
  }

  public long getNativeObjectReference()
  {
    return this.nativeObjectReference;
  }

  public int hashCode()
  {
    return (int)this.nativeObjectReference;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (paramObject.getClass() != getClass()))
      return false;
    BrowserSideObject localBrowserSideObject = (BrowserSideObject)paramObject;
    return this.nativeObjectReference == localBrowserSideObject.nativeObjectReference;
  }

  public String toString()
  {
    return "[BrowserSideObject 0x" + Long.toHexString(this.nativeObjectReference) + "]";
  }

  public static void write(Serializer paramSerializer, BrowserSideObject paramBrowserSideObject)
    throws IOException
  {
    if (paramBrowserSideObject == null)
    {
      paramSerializer.writeBoolean(false);
    }
    else
    {
      paramSerializer.writeBoolean(true);
      paramSerializer.writeLong(paramBrowserSideObject.getNativeObjectReference());
    }
  }

  public static BrowserSideObject read(Serializer paramSerializer)
    throws IOException
  {
    if (!paramSerializer.readBoolean())
      return null;
    return new BrowserSideObject(paramSerializer.readLong());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.liveconnect.BrowserSideObject
 * JD-Core Version:    0.6.2
 */