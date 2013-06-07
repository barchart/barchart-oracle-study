package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import java.lang.reflect.Type;

public abstract interface RuntimeMapPropertyInfo extends RuntimePropertyInfo, MapPropertyInfo<Type, Class>
{
  public abstract RuntimeNonElement getKeyType();

  public abstract RuntimeNonElement getValueType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimeMapPropertyInfo
 * JD-Core Version:    0.6.2
 */