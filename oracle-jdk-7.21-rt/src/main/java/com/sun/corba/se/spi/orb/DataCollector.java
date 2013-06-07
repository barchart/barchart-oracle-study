package com.sun.corba.se.spi.orb;

import java.util.Properties;

public abstract interface DataCollector
{
  public abstract boolean isApplet();

  public abstract boolean initialHostIsLocal();

  public abstract void setParser(PropertyParser paramPropertyParser);

  public abstract Properties getProperties();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orb.DataCollector
 * JD-Core Version:    0.6.2
 */