package sun.plugin2.ipc.unix;

import com.sun.deploy.net.socket.UnixDomainSocket;
import com.sun.deploy.net.socket.UnixDomainSocketException;
import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import sun.plugin2.ipc.NamedPipe;

public class DomainSocketNamedPipe extends NamedPipe
{
  private String sockServerName;
  private UnixDomainSocket sockServer;
  private boolean iAmServer;
  private Object connectionSync = new Object();
  private boolean connectFailed = false;
  private IOException connectException = null;
  private volatile UnixDomainSocket sockClient;
  private boolean connectionThreadDone = false;

  public DomainSocketNamedPipe(String paramString)
  {
    if (!UnixDomainSocket.isSupported())
      throw new RuntimeException("UnixDomainSocket not supported");
    this.sockServerName = paramString;
    this.sockServer = null;
    this.iAmServer = (null == paramString);
    this.sockClient = null;
    if (this.iAmServer)
      try
      {
        this.sockServer = UnixDomainSocket.CreateServerBindListen(0, 1);
      }
      catch (UnixDomainSocketException localUnixDomainSocketException)
      {
        throw new RuntimeException("Error creating AF_UNIX: " + localUnixDomainSocketException.getMessage());
      }
    startConnectThread();
  }

  private void startConnectThread()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          if (DomainSocketNamedPipe.this.iAmServer)
          {
            DomainSocketNamedPipe.this.sockClient = DomainSocketNamedPipe.this.sockServer.accept();
          }
          else
          {
            DomainSocketNamedPipe.this.sockClient = UnixDomainSocket.CreateClientConnect(DomainSocketNamedPipe.this.sockServerName, false, 0);
            DomainSocketNamedPipe.this.sockClient.deleteFileOnClose();
          }
          synchronized (DomainSocketNamedPipe.this.connectionSync)
          {
            DomainSocketNamedPipe.this.connectionThreadDone = true;
            DomainSocketNamedPipe.this.connectionSync.notifyAll();
          }
        }
        catch (IOException localIOException)
        {
          synchronized (DomainSocketNamedPipe.this.connectionSync)
          {
            DomainSocketNamedPipe.this.connectException = localIOException;
            DomainSocketNamedPipe.this.connectFailed = true;
            DomainSocketNamedPipe.this.connectionThreadDone = true;
            DomainSocketNamedPipe.this.connectionSync.notifyAll();
          }
        }
      }
    }
    , "Pipe Connector Thread").start();
  }

  private void waitForConnectionThread()
    throws IOException
  {
    if (!this.connectionThreadDone)
      synchronized (this.connectionSync)
      {
        while (!this.connectionThreadDone)
          try
          {
            this.connectionSync.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
      }
    if (this.connectFailed)
    {
      if (this.connectException != null)
        throw this.connectException;
      throw new IOException("Never received connection from client side");
    }
  }

  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    waitForConnectionThread();
    int i = 0;
    while (i == 0)
    {
      long l1 = SystemUtils.microTime();
      int j;
      try
      {
        j = this.sockClient.read(paramByteBuffer);
      }
      catch (UnixDomainSocketException localUnixDomainSocketException)
      {
        long l2 = SystemUtils.microTime();
        throw new IOException("Error reading from AF_UNIX: " + localUnixDomainSocketException.getMessage() + ", read ts: " + l1 + ", now ts: " + l2 + ", dT " + (l2 - l1));
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        break;
      }
      i += j;
    }
    return i;
  }

  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    waitForConnectionThread();
    int i = 0;
    while (paramByteBuffer.hasRemaining())
    {
      long l1 = SystemUtils.microTime();
      int j;
      try
      {
        j = this.sockClient.write(paramByteBuffer);
      }
      catch (UnixDomainSocketException localUnixDomainSocketException)
      {
        long l2 = SystemUtils.microTime();
        throw new IOException("Error writing to AF_UNIX: " + localUnixDomainSocketException.getMessage() + ", write ts: " + l1 + ", now ts: " + l2 + ", dT " + (l2 - l1));
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        break;
      }
      i += j;
    }
    return i;
  }

  public void close()
    throws IOException
  {
    if (null != this.sockClient)
      this.sockClient.close();
    if (null != this.sockServer)
      this.sockServer.close();
    synchronized (this.connectionSync)
    {
      this.connectFailed = true;
      this.connectionThreadDone = true;
      this.connectionSync.notifyAll();
    }
  }

  protected void finalize()
    throws Throwable
  {
    try
    {
      close();
    }
    finally
    {
      super.finalize();
    }
  }

  public boolean isOpen()
  {
    return (null != this.sockClient) && (this.sockClient.isOpen());
  }

  public String toString()
  {
    if (this.iAmServer)
      return "UnixNamedPipe: serverSocket: " + this.sockServer + ", clientSocket: " + this.sockClient;
    return "UnixNamedPipe: clientSocket: " + this.sockClient;
  }

  public Map getChildProcessParameters()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("write_pipe_name", this.sockServer.getFilename());
    return localHashMap;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.unix.DomainSocketNamedPipe
 * JD-Core Version:    0.6.2
 */