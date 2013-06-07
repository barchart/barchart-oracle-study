package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public abstract interface XSGrammarPoolContainer
{
  public abstract XMLGrammarPool getGrammarPool();

  public abstract boolean isFullyComposed();

  public abstract Boolean getFeature(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer
 * JD-Core Version:    0.6.2
 */