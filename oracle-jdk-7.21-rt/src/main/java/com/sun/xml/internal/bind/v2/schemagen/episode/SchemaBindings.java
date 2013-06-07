package com.sun.xml.internal.bind.v2.schemagen.episode;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface SchemaBindings extends TypedXmlWriter
{
  @XmlAttribute
  public abstract void map(boolean paramBoolean);

  @XmlElement("package")
  public abstract Package _package();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.schemagen.episode.SchemaBindings
 * JD-Core Version:    0.6.2
 */