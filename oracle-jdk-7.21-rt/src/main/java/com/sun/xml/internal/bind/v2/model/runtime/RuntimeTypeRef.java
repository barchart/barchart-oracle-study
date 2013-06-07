package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import java.lang.reflect.Type;

public abstract interface RuntimeTypeRef extends TypeRef<Type, Class>, RuntimeNonElementRef
{
  public abstract RuntimeNonElement getTarget();

  public abstract RuntimePropertyInfo getSource();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef
 * JD-Core Version:    0.6.2
 */