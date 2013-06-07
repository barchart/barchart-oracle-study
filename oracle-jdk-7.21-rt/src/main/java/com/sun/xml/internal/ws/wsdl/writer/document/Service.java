package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("service")
public abstract interface Service extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract Service name(String paramString);

  @XmlElement
  public abstract Port port();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.wsdl.writer.document.Service
 * JD-Core Version:    0.6.2
 */