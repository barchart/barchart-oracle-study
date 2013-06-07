package com.sun.xml.internal.bind.v2.model.core;

import java.util.Set;

public abstract interface RegistryInfo<T, C>
{
  public abstract Set<TypeInfo<T, C>> getReferences();

  public abstract C getClazz();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.RegistryInfo
 * JD-Core Version:    0.6.2
 */