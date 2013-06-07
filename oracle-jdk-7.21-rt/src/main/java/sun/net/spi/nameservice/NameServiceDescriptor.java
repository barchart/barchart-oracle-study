package sun.net.spi.nameservice;

public abstract interface NameServiceDescriptor
{
  public abstract NameService createNameService()
    throws Exception;

  public abstract String getProviderName();

  public abstract String getType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.spi.nameservice.NameServiceDescriptor
 * JD-Core Version:    0.6.2
 */