package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public abstract interface XMLEventAllocator
{
  public abstract XMLEventAllocator newInstance();

  public abstract XMLEvent allocate(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException;

  public abstract void allocate(XMLStreamReader paramXMLStreamReader, XMLEventConsumer paramXMLEventConsumer)
    throws XMLStreamException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.util.XMLEventAllocator
 * JD-Core Version:    0.6.2
 */