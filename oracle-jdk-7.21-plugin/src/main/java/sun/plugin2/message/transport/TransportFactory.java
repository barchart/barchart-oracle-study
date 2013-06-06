package sun.plugin2.message.transport;

import java.io.IOException;

public abstract class TransportFactory
{
  public static final int NAMED_PIPE = 2;

  public static TransportFactory create(int paramInt)
    throws IOException
  {
    switch (paramInt)
    {
    case 2:
      return new NamedPipeTransportFactory();
    }
    throw new IllegalArgumentException("Unknown type " + paramInt);
  }

  public static TransportFactory createForCurrentOS()
    throws IOException
  {
    return create(2);
  }

  public static TransportFactory create(int paramInt, String[] paramArrayOfString)
    throws IOException
  {
    switch (paramInt)
    {
    case 2:
      return new NamedPipeTransportFactory(paramArrayOfString);
    }
    throw new IllegalArgumentException("Unknown type " + paramInt);
  }

  public static TransportFactory createForCurrentOS(String[] paramArrayOfString)
    throws IOException
  {
    return create(2, paramArrayOfString);
  }

  public abstract String[] getChildProcessParameters();

  public abstract SerializingTransport getTransport();

  public abstract void dispose()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.transport.TransportFactory
 * JD-Core Version:    0.6.2
 */