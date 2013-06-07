package com.sun.org.apache.xml.internal.security.encryption;

import org.w3c.dom.Attr;

public abstract interface CipherReference
{
  public abstract String getURI();

  public abstract Attr getURIAsAttr();

  public abstract Transforms getTransforms();

  public abstract void setTransforms(Transforms paramTransforms);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.encryption.CipherReference
 * JD-Core Version:    0.6.2
 */