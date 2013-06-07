package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("element")
public abstract interface LocalElement extends Element, Occurs, TypedXmlWriter
{
  @XmlAttribute
  public abstract LocalElement form(String paramString);

  @XmlAttribute
  public abstract LocalElement name(String paramString);

  @XmlAttribute
  public abstract LocalElement ref(QName paramQName);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement
 * JD-Core Version:    0.6.2
 */