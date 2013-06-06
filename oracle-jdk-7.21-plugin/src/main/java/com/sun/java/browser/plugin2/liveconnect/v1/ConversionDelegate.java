package com.sun.java.browser.plugin2.liveconnect.v1;

public abstract interface ConversionDelegate
{
  public abstract int conversionCost(Object paramObject1, Object paramObject2);

  public abstract boolean convert(Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.java.browser.plugin2.liveconnect.v1.ConversionDelegate
 * JD-Core Version:    0.6.2
 */