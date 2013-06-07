package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface Annotated extends TypedXmlWriter
{
  @XmlElement
  public abstract Annotation annotation();

  @XmlAttribute
  public abstract Annotated id(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.Annotated
 * JD-Core Version:    0.6.2
 */