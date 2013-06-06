package com.sun.java.browser.plugin2.liveconnect.v1;

public abstract interface Bridge
{
  public abstract void register(InvocationDelegate paramInvocationDelegate);

  public abstract void unregister(InvocationDelegate paramInvocationDelegate);

  public abstract void register(ConversionDelegate paramConversionDelegate);

  public abstract void unregister(ConversionDelegate paramConversionDelegate);

  public abstract int conversionCost(Object paramObject1, Object paramObject2);

  public abstract Object convert(Object paramObject1, Object paramObject2)
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.java.browser.plugin2.liveconnect.v1.Bridge
 * JD-Core Version:    0.6.2
 */