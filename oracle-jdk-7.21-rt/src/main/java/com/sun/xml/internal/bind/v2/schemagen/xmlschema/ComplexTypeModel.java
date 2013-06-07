package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface ComplexTypeModel extends AttrDecls, TypeDefParticle, TypedXmlWriter
{
  @XmlElement
  public abstract SimpleContent simpleContent();

  @XmlElement
  public abstract ComplexContent complexContent();

  @XmlAttribute
  public abstract ComplexTypeModel mixed(boolean paramBoolean);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexTypeModel
 * JD-Core Version:    0.6.2
 */