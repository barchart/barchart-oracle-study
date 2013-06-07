package com.sun.corba.se.pept.protocol;

import java.util.Iterator;

public abstract interface ClientInvocationInfo
{
  public abstract Iterator getContactInfoListIterator();

  public abstract void setContactInfoListIterator(Iterator paramIterator);

  public abstract boolean isRetryInvocation();

  public abstract void setIsRetryInvocation(boolean paramBoolean);

  public abstract int getEntryCount();

  public abstract void incrementEntryCount();

  public abstract void decrementEntryCount();

  public abstract void setClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher);

  public abstract ClientRequestDispatcher getClientRequestDispatcher();

  public abstract void setMessageMediator(MessageMediator paramMessageMediator);

  public abstract MessageMediator getMessageMediator();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.pept.protocol.ClientInvocationInfo
 * JD-Core Version:    0.6.2
 */