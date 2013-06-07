package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("binding")
public abstract interface SOAPBinding extends TypedXmlWriter
{
  @XmlAttribute
  public abstract SOAPBinding transport(String paramString);

  @XmlAttribute
  public abstract SOAPBinding style(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding
 * JD-Core Version:    0.6.2
 */