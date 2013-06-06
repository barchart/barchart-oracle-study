package sun.plugin2.message.transport;

import java.io.IOException;
import sun.plugin2.ipc.IPCFactory;
import sun.plugin2.ipc.NamedPipe;

class NamedPipeTransportFactory extends TransportFactory
{
  private NamedPipe namedPipe;
  private NamedPipeTransport transport;

  NamedPipeTransportFactory()
    throws IOException
  {
    this.namedPipe = IPCFactory.getFactory().createNamedPipe(null);
    this.transport = new NamedPipeTransport(this.namedPipe);
  }

  NamedPipeTransportFactory(String[] paramArrayOfString)
    throws IOException
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
      throw new IOException("Invalid parameters");
    this.namedPipe = IPCFactory.getFactory().createNamedPipe(IPCFactory.stringToMap(paramArrayOfString[0]));
    if (this.namedPipe == null)
      throw new IOException("Invalid parameters");
    this.transport = new NamedPipeTransport(this.namedPipe);
  }

  public String[] getChildProcessParameters()
  {
    return new String[] { IPCFactory.mapToString(this.namedPipe.getChildProcessParameters()) };
  }

  public SerializingTransport getTransport()
  {
    return this.transport;
  }

  public void dispose()
    throws IOException
  {
    if (this.transport != null)
    {
      this.transport.shutdown();
      this.transport = null;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.transport.NamedPipeTransportFactory
 * JD-Core Version:    0.6.2
 */