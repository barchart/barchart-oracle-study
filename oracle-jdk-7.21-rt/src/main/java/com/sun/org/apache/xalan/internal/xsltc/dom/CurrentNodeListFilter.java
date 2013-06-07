package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract interface CurrentNodeListFilter
{
  public abstract boolean test(int paramInt1, int paramInt2, int paramInt3, int paramInt4, AbstractTranslet paramAbstractTranslet, DTMAxisIterator paramDTMAxisIterator);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter
 * JD-Core Version:    0.6.2
 */