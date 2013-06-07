package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface ComplexTypeHost extends TypeHost, TypedXmlWriter
{
  @XmlElement
  public abstract ComplexType complexType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexTypeHost
 * JD-Core Version:    0.6.2
 */