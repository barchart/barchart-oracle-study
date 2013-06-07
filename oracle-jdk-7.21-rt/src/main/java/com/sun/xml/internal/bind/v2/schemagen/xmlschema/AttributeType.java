package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public abstract interface AttributeType extends SimpleTypeHost, TypedXmlWriter
{
  @XmlAttribute
  public abstract AttributeType type(QName paramQName);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType
 * JD-Core Version:    0.6.2
 */