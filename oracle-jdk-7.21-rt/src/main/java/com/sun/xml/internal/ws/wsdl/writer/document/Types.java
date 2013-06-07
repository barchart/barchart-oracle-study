package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;

@XmlElement("types")
public abstract interface Types extends TypedXmlWriter, Documented
{
  @XmlElement(value="schema", ns="http://www.w3.org/2001/XMLSchema")
  public abstract Schema schema();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.wsdl.writer.document.Types
 * JD-Core Version:    0.6.2
 */