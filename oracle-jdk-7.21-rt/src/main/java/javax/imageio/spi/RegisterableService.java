package javax.imageio.spi;

public abstract interface RegisterableService
{
  public abstract void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);

  public abstract void onDeregistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.imageio.spi.RegisterableService
 * JD-Core Version:    0.6.2
 */