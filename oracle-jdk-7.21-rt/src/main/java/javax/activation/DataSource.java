package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface DataSource
{
  public abstract InputStream getInputStream()
    throws IOException;

  public abstract OutputStream getOutputStream()
    throws IOException;

  public abstract String getContentType();

  public abstract String getName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.DataSource
 * JD-Core Version:    0.6.2
 */