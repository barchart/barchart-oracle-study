package javax.xml.ws.handler;

public abstract interface Handler<C extends MessageContext>
{
  public abstract boolean handleMessage(C paramC);

  public abstract boolean handleFault(C paramC);

  public abstract void close(MessageContext paramMessageContext);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.handler.Handler
 * JD-Core Version:    0.6.2
 */