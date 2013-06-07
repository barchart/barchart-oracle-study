package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import java.lang.reflect.Type;

public abstract interface RuntimeAttributePropertyInfo extends AttributePropertyInfo<Type, Class>, RuntimePropertyInfo, RuntimeNonElementRef
{
  public abstract RuntimeNonElement getTarget();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo
 * JD-Core Version:    0.6.2
 */