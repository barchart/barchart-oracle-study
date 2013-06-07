package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;

/** @deprecated */
public abstract interface Pipe
{
  public abstract Packet process(Packet paramPacket);

  public abstract void preDestroy();

  public abstract Pipe copy(PipeCloner paramPipeCloner);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.pipe.Pipe
 * JD-Core Version:    0.6.2
 */