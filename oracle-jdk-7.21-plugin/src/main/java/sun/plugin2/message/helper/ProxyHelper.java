package sun.plugin2.message.helper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import sun.plugin2.message.Serializer;

public final class ProxyHelper
{
  private static final int DIRECT_PROXY = 0;
  private static final int HTTP_PROXY = 1;
  private static final int SOCKS_PROXY = 2;

  private static int getProxyTypeID(Proxy.Type paramType)
    throws IllegalArgumentException
  {
    if (paramType == Proxy.Type.DIRECT)
      return 0;
    if (paramType == Proxy.Type.HTTP)
      return 1;
    if (paramType == Proxy.Type.SOCKS)
      return 2;
    throw new IllegalArgumentException();
  }

  private static Proxy.Type getProxyType(int paramInt)
    throws IllegalArgumentException
  {
    switch (paramInt)
    {
    case 0:
      return Proxy.Type.DIRECT;
    case 1:
      return Proxy.Type.HTTP;
    case 2:
      return Proxy.Type.SOCKS;
    }
    throw new IllegalArgumentException();
  }

  public static void write(Serializer paramSerializer, Proxy paramProxy)
    throws IOException
  {
    if (paramProxy == null)
    {
      paramSerializer.writeBoolean(false);
      return;
    }
    paramSerializer.writeBoolean(true);
    paramSerializer.writeInt(getProxyTypeID(paramProxy.type()));
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramProxy.address();
    if (localInetSocketAddress != null)
    {
      paramSerializer.writeBoolean(true);
      paramSerializer.writeUTF(localInetSocketAddress.getHostName());
      paramSerializer.writeInt(localInetSocketAddress.getPort());
    }
    else
    {
      paramSerializer.writeBoolean(false);
    }
  }

  public static Proxy read(Serializer paramSerializer)
    throws IOException
  {
    if (!paramSerializer.readBoolean())
      return null;
    Proxy.Type localType = getProxyType(paramSerializer.readInt());
    InetSocketAddress localInetSocketAddress = null;
    if (paramSerializer.readBoolean())
      localInetSocketAddress = new InetSocketAddress(paramSerializer.readUTF(), paramSerializer.readInt());
    if (localType == Proxy.Type.DIRECT)
      return Proxy.NO_PROXY;
    return new Proxy(localType, localInetSocketAddress);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.helper.ProxyHelper
 * JD-Core Version:    0.6.2
 */