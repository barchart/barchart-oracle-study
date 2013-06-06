package sun.plugin2.message.transport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import sun.plugin2.ipc.NamedPipe;
import sun.plugin2.message.AbstractSerializer;
import sun.plugin2.message.Serializer;

public class NamedPipeTransport extends SerializingTransport
{
  private static final int BUFFER_SIZE = 8192;
  private volatile NamedPipe namedPipe;
  private ByteBuffer input;
  private ByteBuffer output;
  private SerializerImpl serializer;

  public NamedPipeTransport(NamedPipe paramNamedPipe)
    throws IOException
  {
    this.namedPipe = paramNamedPipe;
    this.input = ByteBuffer.allocateDirect(8192);
    this.input.order(ByteOrder.nativeOrder());
    this.input.limit(0);
    this.output = ByteBuffer.allocateDirect(8192);
    this.output.order(ByteOrder.nativeOrder());
    this.serializer = new SerializerImpl();
  }

  public synchronized void shutdown()
  {
    try
    {
      if (this.namedPipe != null)
      {
        this.namedPipe.close();
        this.namedPipe = null;
      }
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void waitForData(long paramLong)
    throws IOException
  {
    this.serializer.waitForData();
  }

  protected void signalDataWritten()
    throws IOException
  {
    getSerializer().flush();
  }

  protected void signalDataRead()
  {
  }

  protected boolean isDataAvailable()
    throws IOException
  {
    return true;
  }

  protected Serializer getSerializer()
  {
    return this.serializer;
  }

  public String toString()
  {
    if (null == this.namedPipe)
      return "NamedPipe[shutdown]";
    return this.namedPipe.toString();
  }

  class SerializerImpl extends AbstractSerializer
  {
    SerializerImpl()
    {
    }

    void waitForData()
      throws IOException
    {
      if (NamedPipeTransport.this.input.remaining() > 0)
        return;
      read();
    }

    void read()
      throws IOException
    {
      NamedPipeTransport.this.input.rewind();
      NamedPipeTransport.this.input.limit(NamedPipeTransport.this.input.capacity());
      if (null == NamedPipeTransport.this.namedPipe)
        throw new IOException("namedPipe shutdown");
      NamedPipeTransport.this.namedPipe.read(NamedPipeTransport.this.input);
      NamedPipeTransport.this.input.flip();
    }

    public void writeByte(byte paramByte)
      throws IOException
    {
      if (NamedPipeTransport.this.output.remaining() < 1)
        flush();
      NamedPipeTransport.this.output.put(paramByte);
    }

    public void flush()
      throws IOException
    {
      if (NamedPipeTransport.this.output.position() == 0)
        return;
      NamedPipeTransport.this.output.flip();
      if (null == NamedPipeTransport.this.namedPipe)
        throw new IOException("namedPipe shutdown");
      NamedPipeTransport.this.namedPipe.write(NamedPipeTransport.this.output);
      NamedPipeTransport.this.output.rewind();
      NamedPipeTransport.this.output.limit(NamedPipeTransport.this.output.capacity());
    }

    public byte readByte()
      throws IOException
    {
      if (NamedPipeTransport.this.input.remaining() > 0)
        return NamedPipeTransport.this.input.get();
      read();
      return NamedPipeTransport.this.input.get();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.transport.NamedPipeTransport
 * JD-Core Version:    0.6.2
 */