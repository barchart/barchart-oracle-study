package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("attribute")
public abstract interface TopLevelAttribute extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter
{
  @XmlAttribute
  public abstract TopLevelAttribute name(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelAttribute
 * JD-Core Version:    0.6.2
 */