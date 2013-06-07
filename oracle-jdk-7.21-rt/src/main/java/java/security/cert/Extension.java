package java.security.cert;

import java.io.IOException;
import java.io.OutputStream;

public abstract interface Extension
{
  public abstract String getId();

  public abstract boolean isCritical();

  public abstract byte[] getValue();

  public abstract void encode(OutputStream paramOutputStream)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.Extension
 * JD-Core Version:    0.6.2
 */