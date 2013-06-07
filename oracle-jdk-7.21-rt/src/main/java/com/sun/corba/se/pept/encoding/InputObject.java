package com.sun.corba.se.pept.encoding;

import com.sun.corba.se.pept.protocol.MessageMediator;
import java.io.IOException;

public abstract interface InputObject
{
  public abstract void setMessageMediator(MessageMediator paramMessageMediator);

  public abstract MessageMediator getMessageMediator();

  public abstract void close()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.pept.encoding.InputObject
 * JD-Core Version:    0.6.2
 */