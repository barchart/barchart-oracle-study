package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("element")
public abstract interface TopLevelElement extends Element, TypedXmlWriter
{
  @XmlAttribute("final")
  public abstract TopLevelElement _final(String paramString);

  @XmlAttribute("final")
  public abstract TopLevelElement _final(String[] paramArrayOfString);

  @XmlAttribute("abstract")
  public abstract TopLevelElement _abstract(boolean paramBoolean);

  @XmlAttribute
  public abstract TopLevelElement substitutionGroup(QName paramQName);

  @XmlAttribute
  public abstract TopLevelElement name(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelElement
 * JD-Core Version:    0.6.2
 */