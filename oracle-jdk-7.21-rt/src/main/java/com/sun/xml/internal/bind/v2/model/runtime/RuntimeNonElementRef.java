package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElementRef;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;

public abstract interface RuntimeNonElementRef extends NonElementRef<Type, Class>
{
  public abstract RuntimeNonElement getTarget();

  public abstract RuntimePropertyInfo getSource();

  public abstract Transducer getTransducer();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef
 * JD-Core Version:    0.6.2
 */