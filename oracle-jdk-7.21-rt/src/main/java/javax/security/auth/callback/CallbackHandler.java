package javax.security.auth.callback;

import java.io.IOException;

public abstract interface CallbackHandler
{
  public abstract void handle(Callback[] paramArrayOfCallback)
    throws IOException, UnsupportedCallbackException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.callback.CallbackHandler
 * JD-Core Version:    0.6.2
 */