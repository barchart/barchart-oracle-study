package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public abstract interface Wildcard extends Annotated, TypedXmlWriter
{
  @XmlAttribute
  public abstract Wildcard processContents(String paramString);

  @XmlAttribute
  public abstract Wildcard namespace(String[] paramArrayOfString);

  @XmlAttribute
  public abstract Wildcard namespace(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.Wildcard
 * JD-Core Version:    0.6.2
 */