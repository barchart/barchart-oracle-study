package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public abstract interface XMLDTDValidatorFilter extends XMLDocumentFilter
{
  public abstract boolean hasGrammar();

  public abstract boolean validate();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter
 * JD-Core Version:    0.6.2
 */