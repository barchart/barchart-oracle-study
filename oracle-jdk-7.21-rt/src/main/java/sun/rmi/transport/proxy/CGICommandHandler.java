package sun.rmi.transport.proxy;

abstract interface CGICommandHandler
{
  public abstract String getName();

  public abstract void execute(String paramString)
    throws CGIClientException, CGIServerException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.CGICommandHandler
 * JD-Core Version:    0.6.2
 */