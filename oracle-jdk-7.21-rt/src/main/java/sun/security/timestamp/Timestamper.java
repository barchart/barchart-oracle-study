package sun.security.timestamp;

import java.io.IOException;

public abstract interface Timestamper
{
  public abstract TSResponse generateTimestamp(TSRequest paramTSRequest)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.timestamp.Timestamper
 * JD-Core Version:    0.6.2
 */