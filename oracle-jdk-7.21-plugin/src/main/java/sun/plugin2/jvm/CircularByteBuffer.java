package sun.plugin2.jvm;

import com.sun.deploy.trace.Trace;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CircularByteBuffer
{
  private static final boolean DEBUG = false;
  private byte[] buf;
  private int iRead = 0;
  private int iWrite = 0;
  private int lenRead = 0;

  public CircularByteBuffer(int paramInt)
  {
    this.buf = new byte[paramInt];
  }

  private synchronized void updateAvailableReadLength(boolean paramBoolean)
  {
    if (this.iWrite == this.iRead)
    {
      if (paramBoolean)
        this.lenRead = this.buf.length;
      else
        this.lenRead = 0;
    }
    else if (this.iWrite > this.iRead)
      this.lenRead = (this.iWrite - this.iRead);
    else
      this.lenRead = (this.buf.length - this.iRead + this.iWrite);
  }

  private synchronized int availableToWrite()
  {
    return this.buf.length - this.lenRead;
  }

  private synchronized int availableToRead()
  {
    return this.lenRead;
  }

  private synchronized void truncateRead(int paramInt)
  {
    if (this.iRead + paramInt >= this.buf.length)
      this.iRead = (this.iRead + paramInt - this.buf.length);
    else
      this.iRead += paramInt;
  }

  public synchronized void write(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    if (paramInt > paramArrayOfByte.length)
      paramInt = paramArrayOfByte.length;
    if (paramInt == 0)
      return;
    int i = this.iWrite;
    int j = availableToWrite();
    int k = 0;
    int m;
    if (paramInt >= this.buf.length)
    {
      k = paramInt - this.buf.length;
      paramInt = this.buf.length;
      this.iWrite = this.iRead;
    }
    else
    {
      if (paramInt > j)
      {
        m = paramInt - j;
        truncateRead(m);
      }
      this.iWrite = ((this.iWrite + paramInt) % this.buf.length);
    }
    updateAvailableReadLength(true);
    if (i + paramInt <= this.buf.length)
    {
      System.arraycopy(paramArrayOfByte, k, this.buf, i, paramInt);
    }
    else
    {
      m = this.buf.length - i;
      int n = paramInt - m;
      System.arraycopy(paramArrayOfByte, k, this.buf, i, m);
      System.arraycopy(paramArrayOfByte, k + m, this.buf, 0, n);
    }
  }

  public synchronized byte[] chunkRead(int paramInt)
  {
    int i = availableToRead();
    if (i > paramInt)
      i = paramInt;
    byte[] arrayOfByte = new byte[i];
    if (this.iRead + i <= this.buf.length)
    {
      System.arraycopy(this.buf, this.iRead, arrayOfByte, 0, i);
      this.iRead += i;
    }
    else
    {
      int j = this.buf.length - this.iRead;
      int k = this.iRead + i - this.buf.length;
      System.arraycopy(this.buf, this.iRead, arrayOfByte, 0, j);
      System.arraycopy(this.buf, 0, arrayOfByte, j, k);
      this.iRead = k;
    }
    updateAvailableReadLength(false);
    return arrayOfByte;
  }

  public static class Streamer
    implements Runnable
  {
    private static final int BUFSIZE = 4096;
    private final InputStream in;
    private final CircularByteBuffer buf;
    private final int readSize;
    private boolean done = false;
    private boolean running = false;
    private boolean pauseForConsumer = false;

    public Streamer(InputStream paramInputStream)
    {
      this(paramInputStream, paramInputStream instanceof ByteArrayInputStream);
    }

    public Streamer(InputStream paramInputStream, boolean paramBoolean)
    {
      this(paramInputStream, 8192, 4096, paramBoolean);
    }

    public Streamer(InputStream paramInputStream, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.in = paramInputStream;
      this.buf = new CircularByteBuffer(paramInt1);
      this.readSize = paramInt2;
      this.pauseForConsumer = paramBoolean;
    }

    public void run()
    {
      byte[] arrayOfByte = new byte[this.readSize];
      try
      {
        synchronized (this)
        {
          this.running = true;
        }
        while (!this.done)
        {
          if (this.pauseForConsumer)
            waitForReader();
          int i = this.in.read(arrayOfByte);
          if (i < 0)
            break;
          this.buf.write(arrayOfByte, i);
        }
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
      }
      finally
      {
        this.done = true;
      }
    }

    private boolean waitForRunning()
    {
      while (!this.running)
        try
        {
          wait(10L);
        }
        catch (InterruptedException localInterruptedException)
        {
          return false;
        }
      return true;
    }

    public byte[] readAvailable()
    {
      synchronized (this)
      {
        if (!waitForRunning())
          return new byte[0];
      }
      ??? = this.buf.chunkRead(this.readSize);
      if (this.pauseForConsumer)
        synchronized (this)
        {
          notifyAll();
        }
      if ((this.done) && ((??? == null) || (???.length == 0)))
        return null;
      return ???;
    }

    private void waitForReader()
    {
      while (this.buf.availableToWrite() < this.readSize)
        synchronized (this)
        {
          try
          {
            wait(10L);
          }
          catch (InterruptedException localInterruptedException)
          {
          }
        }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.jvm.CircularByteBuffer
 * JD-Core Version:    0.6.2
 */