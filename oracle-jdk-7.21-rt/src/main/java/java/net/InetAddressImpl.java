package java.net;

import java.io.IOException;

abstract interface InetAddressImpl
{
  public abstract String getLocalHostName()
    throws UnknownHostException;

  public abstract InetAddress[] lookupAllHostAddr(String paramString)
    throws UnknownHostException;

  public abstract String getHostByAddr(byte[] paramArrayOfByte)
    throws UnknownHostException;

  public abstract InetAddress anyLocalAddress();

  public abstract InetAddress loopbackAddress();

  public abstract boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.InetAddressImpl
 * JD-Core Version:    0.6.2
 */