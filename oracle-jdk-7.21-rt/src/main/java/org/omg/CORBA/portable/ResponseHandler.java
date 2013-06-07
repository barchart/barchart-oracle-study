package org.omg.CORBA.portable;

public abstract interface ResponseHandler
{
  public abstract OutputStream createReply();

  public abstract OutputStream createExceptionReply();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.portable.ResponseHandler
 * JD-Core Version:    0.6.2
 */