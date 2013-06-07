package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Type;
import java.util.Collection;

public abstract interface RuntimePropertyInfo extends PropertyInfo<Type, Class>
{
  public abstract Collection<? extends RuntimeTypeInfo> ref();

  public abstract Accessor getAccessor();

  public abstract boolean elementOnlyContent();

  public abstract Type getRawType();

  public abstract Type getIndividualType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo
 * JD-Core Version:    0.6.2
 */