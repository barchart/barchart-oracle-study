package javax.jnlp;

public abstract interface ServiceManagerStub
{
  public abstract Object lookup(String paramString)
    throws UnavailableServiceException;

  public abstract String[] getServiceNames();
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     javax.jnlp.ServiceManagerStub
 * JD-Core Version:    0.6.2
 */