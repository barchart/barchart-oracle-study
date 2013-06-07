package sun.net.spi.nameservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract interface NameService
{
  public abstract InetAddress[] lookupAllHostAddr(String paramString)
    throws UnknownHostException;

  public abstract String getHostByAddr(byte[] paramArrayOfByte)
    throws UnknownHostException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.spi.nameservice.NameService
 * JD-Core Version:    0.6.2
 */