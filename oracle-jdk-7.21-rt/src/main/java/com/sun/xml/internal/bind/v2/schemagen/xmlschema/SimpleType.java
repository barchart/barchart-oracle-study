package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("simpleType")
public abstract interface SimpleType extends Annotated, SimpleDerivation, TypedXmlWriter
{
  @XmlAttribute("final")
  public abstract SimpleType _final(String paramString);

  @XmlAttribute("final")
  public abstract SimpleType _final(String[] paramArrayOfString);

  @XmlAttribute
  public abstract SimpleType name(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleType
 * JD-Core Version:    0.6.2
 */