package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.MarshalledObject;

public abstract interface Unmarshal
{
  public abstract Object get(MarshalledObject<?> paramMarshalledObject)
    throws IOException, ClassNotFoundException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.internal.Unmarshal
 * JD-Core Version:    0.6.2
 */