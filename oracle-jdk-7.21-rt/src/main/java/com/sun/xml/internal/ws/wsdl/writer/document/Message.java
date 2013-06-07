package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("message")
public abstract interface Message extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract Message name(String paramString);

  @XmlElement
  public abstract Part part();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.wsdl.writer.document.Message
 * JD-Core Version:    0.6.2
 */