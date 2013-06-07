package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public abstract interface RuntimeElementPropertyInfo extends ElementPropertyInfo<Type, Class>, RuntimePropertyInfo
{
  public abstract Collection<? extends RuntimeTypeInfo> ref();

  public abstract List<? extends RuntimeTypeRef> getTypes();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo
 * JD-Core Version:    0.6.2
 */