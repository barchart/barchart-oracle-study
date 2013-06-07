package com.sun.xml.internal.bind.v2.model.core;

import java.util.Collection;

public abstract interface ElementInfo<T, C> extends Element<T, C>
{
  public abstract ElementPropertyInfo<T, C> getProperty();

  public abstract NonElement<T, C> getContentType();

  public abstract T getContentInMemoryType();

  public abstract T getType();

  public abstract ElementInfo<T, C> getSubstitutionHead();

  public abstract Collection<? extends ElementInfo<T, C>> getSubstitutionMembers();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.ElementInfo
 * JD-Core Version:    0.6.2
 */