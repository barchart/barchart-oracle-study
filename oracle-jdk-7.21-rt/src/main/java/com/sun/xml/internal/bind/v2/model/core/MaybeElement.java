package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface MaybeElement<T, C> extends NonElement<T, C>
{
  public abstract boolean isElement();

  public abstract QName getElementName();

  public abstract Element<T, C> asElement();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.MaybeElement
 * JD-Core Version:    0.6.2
 */