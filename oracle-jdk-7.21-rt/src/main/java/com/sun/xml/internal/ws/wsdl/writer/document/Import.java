package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("import")
public abstract interface Import extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract Import location(String paramString);

  @XmlAttribute
  public abstract Import namespace(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.wsdl.writer.document.Import
 * JD-Core Version:    0.6.2
 */