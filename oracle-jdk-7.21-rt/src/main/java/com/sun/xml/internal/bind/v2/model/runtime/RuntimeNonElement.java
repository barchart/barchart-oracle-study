package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;

public abstract interface RuntimeNonElement extends NonElement<Type, Class>, RuntimeTypeInfo
{
  public abstract <V> Transducer<V> getTransducer();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement
 * JD-Core Version:    0.6.2
 */