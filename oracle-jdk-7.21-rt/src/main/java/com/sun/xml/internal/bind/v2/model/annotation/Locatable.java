package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;

public abstract interface Locatable
{
  public abstract Locatable getUpstream();

  public abstract Location getLocation();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.annotation.Locatable
 * JD-Core Version:    0.6.2
 */