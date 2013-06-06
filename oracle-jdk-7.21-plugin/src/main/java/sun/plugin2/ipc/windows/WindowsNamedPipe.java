package sun.plugin2.ipc.windows;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import sun.plugin2.ipc.NamedPipe;
import sun.plugin2.os.windows.Windows;

public class WindowsNamedPipe extends NamedPipe
{
  private long writeHandle;
  private long readHandle;
  private String writeName;
  private String readName;
  private boolean iAmServer;
  private boolean connected;
  private IOException connectException;
  private boolean connectFailed;
  private IntBuffer numReadBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
  private IntBuffer numWrittenBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();

  public WindowsNamedPipe(long paramLong1, long paramLong2, String paramString1, String paramString2, boolean paramBoolean)
  {
    this.writeHandle = paramLong1;
    this.readHandle = paramLong2;
    this.writeName = paramString1;
    this.readName = paramString2;
    this.iAmServer = paramBoolean;
    if (paramBoolean)
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
          int i;
          if (!Windows.ConnectNamedPipe(WindowsNamedPipe.this.writeHandle, null))
          {
            i = Windows.GetLastError();
            if ((i != 535) && (i != 0))
              throw new IOException("Error " + i + " connecting named pipe");
          }
          if (!Windows.ConnectNamedPipe(WindowsNamedPipe.this.readHandle, null))
          {
            i = Windows.GetLastError();
            if ((i != 535) && (i != 0))
              throw new IOException("Error " + i + " connecting named pipe");
          }
          synchronized (WindowsNamedPipe.this)
          {
            WindowsNamedPipe.this.connected = true;
          }
        }
        catch (IOException i)
        {
          synchronized (WindowsNamedPipe.this)
          {
            WindowsNamedPipe.this.connectException = ((IOException)???);
            WindowsNamedPipe.this.connectFailed = true;
          }
        }
        synchronized (WindowsNamedPipe.this)
        {
          WindowsNamedPipe.this.notifyAll();
        }
      }
    }
    , "Pipe Connector Thread").start();
  }

  private void waitForConnection()
    throws IOException
  {
    if (this.iAmServer)
      synchronized (this)
      {
        while (!this.connected)
        {
          if (this.connectFailed)
          {
            if (this.connectException != null)
              throw this.connectException;
            throw new IOException("Never received connection from client side");
          }
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
        }
      }
  }

  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    waitForConnection();
    int i = paramByteBuffer.remaining();
    int j = 0;
    do
    {
      long l1 = SystemUtils.microTime();
      boolean bool = Windows.ReadFile(this.readHandle, paramByteBuffer, i, this.numReadBuffer, null);
      int k = 0;
      if (!bool)
        k = Windows.GetLastError();
      j = this.numReadBuffer.get(0);
      if (!bool)
      {
        long l2 = SystemUtils.microTime();
        throw new IOException("Error " + k + " reading from " + this + ", numRead " + j + ", ReadFile ts: " + l1 + ", now ts: " + l2 + ", dT " + (l2 - l1));
      }
    }
    while (j == 0);
    paramByteBuffer.position(paramByteBuffer.position() + j);
    return j;
  }

  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    waitForConnection();
    int i = paramByteBuffer.remaining();
    while (paramByteBuffer.hasRemaining())
    {
      long l1 = SystemUtils.microTime();
      boolean bool = Windows.WriteFile(this.writeHandle, paramByteBuffer, i, this.numWrittenBuffer, null);
      int j = 0;
      if (!bool)
        j = Windows.GetLastError();
      int k = this.numWrittenBuffer.get(0);
      if (!bool)
      {
        long l2 = SystemUtils.microTime();
        throw new IOException("Error " + j + " writing to " + this + ": numWritten " + k + ", WriteFile ts: " + l1 + ", now ts: " + l2 + ", dT " + (l2 - l1));
      }
      paramByteBuffer.position(paramByteBuffer.position() + k);
    }
    return i;
  }

  public void close()
    throws IOException
  {
    if ((this.writeHandle == 0L) || (this.readHandle == 0L))
      throw new IOException("Already closed");
    if (this.iAmServer)
    {
      localObject1 = new Thread(new Runnable()
      {
        public void run()
        {
          Windows.DisconnectNamedPipe(WindowsNamedPipe.this.writeHandle);
          Windows.DisconnectNamedPipe(WindowsNamedPipe.this.readHandle);
        }
      }
      , "Pipe Disconnector Thread");
      ((Thread)localObject1).start();
      try
      {
        ((Thread)localObject1).join(500L);
      }
      catch (InterruptedException localInterruptedException1)
      {
      }
    }
    Object localObject1 = new boolean[1];
    Runnable local3 = new Runnable()
    {
      private final boolean[] val$resBox;

      public void run()
      {
        boolean bool1 = Windows.CloseHandle(WindowsNamedPipe.this.writeHandle);
        boolean bool2 = Windows.CloseHandle(WindowsNamedPipe.this.readHandle);
        this.val$resBox[0] = ((bool1) && (bool2) ? 1 : false);
      }
    };
    if (this.iAmServer)
    {
      Thread localThread = new Thread(local3, "Pipe Closer Thread");
      localThread.start();
      try
      {
        localThread.join(500L);
      }
      catch (InterruptedException localInterruptedException2)
      {
      }
    }
    else
    {
      local3.run();
    }
    this.writeHandle = 0L;
    this.readHandle = 0L;
    synchronized (this)
    {
      this.connectFailed = true;
      notifyAll();
    }
    if (localObject1[0] == 0)
      throw new IOException("Error closing " + this);
  }

  public boolean isOpen()
  {
    return (this.writeHandle != 0L) && (this.readHandle != 0L);
  }

  public String toString()
  {
    return "WindowsNamedPipe: server: " + this.iAmServer + "; readPipe: " + this.readName + ", readBufferSz: " + 4096 + "; writePipe: " + this.writeName + ", writeBufferSz: " + 4096;
  }

  public Map getChildProcessParameters()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("write_pipe_name", this.readName);
    localHashMap.put("read_pipe_name", this.writeName);
    return localHashMap;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.windows.WindowsNamedPipe
 * JD-Core Version:    0.6.2
 */