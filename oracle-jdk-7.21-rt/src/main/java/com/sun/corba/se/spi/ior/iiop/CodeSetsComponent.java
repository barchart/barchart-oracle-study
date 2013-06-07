package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.ior.TaggedComponent;

public abstract interface CodeSetsComponent extends TaggedComponent
{
  public abstract CodeSetComponentInfo getCodeSetComponentInfo();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.iiop.CodeSetsComponent
 * JD-Core Version:    0.6.2
 */