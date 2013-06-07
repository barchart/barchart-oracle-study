package com.sun.corba.se.pept.encoding;

import com.sun.corba.se.pept.protocol.MessageMediator;
import java.io.IOException;

public abstract interface OutputObject
{
  public abstract void setMessageMediator(MessageMediator paramMessageMediator);

  public abstract MessageMediator getMessageMediator();

  public abstract void close()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.pept.encoding.OutputObject
 * JD-Core Version:    0.6.2
 */