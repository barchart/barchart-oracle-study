package java.lang;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Process
{
  public abstract OutputStream getOutputStream();

  public abstract InputStream getInputStream();

  public abstract InputStream getErrorStream();

  public abstract int waitFor()
    throws InterruptedException;

  public abstract int exitValue();

  public abstract void destroy();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Process
 * JD-Core Version:    0.6.2
 */