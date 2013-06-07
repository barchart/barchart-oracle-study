package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public abstract interface BodyType extends TypedXmlWriter
{
  @XmlAttribute
  public abstract BodyType encodingStyle(String paramString);

  @XmlAttribute
  public abstract BodyType namespace(String paramString);

  @XmlAttribute
  public abstract BodyType use(String paramString);

  @XmlAttribute
  public abstract BodyType parts(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.wsdl.writer.document.soap.BodyType
 * JD-Core Version:    0.6.2
 */