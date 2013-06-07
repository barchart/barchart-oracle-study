package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public abstract interface Connection
{
  public abstract boolean shouldRegisterReadEvent();

  public abstract boolean shouldRegisterServerReadEvent();

  public abstract boolean read();

  public abstract void close();

  public abstract Acceptor getAcceptor();

  public abstract ContactInfo getContactInfo();

  public abstract EventHandler getEventHandler();

  public abstract boolean isServer();

  public abstract boolean isBusy();

  public abstract long getTimeStamp();

  public abstract void setTimeStamp(long paramLong);

  public abstract void setState(String paramString);

  public abstract void writeLock();

  public abstract void writeUnlock();

  public abstract void sendWithoutLock(OutputObject paramOutputObject);

  public abstract void registerWaiter(MessageMediator paramMessageMediator);

  public abstract InputObject waitForResponse(MessageMediator paramMessageMediator);

  public abstract void unregisterWaiter(MessageMediator paramMessageMediator);

  public abstract void setConnectionCache(ConnectionCache paramConnectionCache);

  public abstract ConnectionCache getConnectionCache();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.pept.transport.Connection
 * JD-Core Version:    0.6.2
 */