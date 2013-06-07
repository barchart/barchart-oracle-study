package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public abstract interface XMLEventConsumer
{
  public abstract void add(XMLEvent paramXMLEvent)
    throws XMLStreamException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.util.XMLEventConsumer
 * JD-Core Version:    0.6.2
 */