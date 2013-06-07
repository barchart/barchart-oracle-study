package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import java.lang.reflect.Type;

public abstract interface RuntimeArrayInfo extends ArrayInfo<Type, Class>, RuntimeNonElement
{
  public abstract Class getType();

  public abstract RuntimeNonElement getItemType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo
 * JD-Core Version:    0.6.2
 */