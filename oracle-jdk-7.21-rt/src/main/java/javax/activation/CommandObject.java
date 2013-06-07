package javax.activation;

import java.io.IOException;

public abstract interface CommandObject
{
  public abstract void setCommandContext(String paramString, DataHandler paramDataHandler)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.CommandObject
 * JD-Core Version:    0.6.2
 */