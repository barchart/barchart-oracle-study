package javax.xml.stream;

import java.util.Iterator;
import javax.xml.stream.events.XMLEvent;

public abstract interface XMLEventReader extends Iterator
{
  public abstract XMLEvent nextEvent()
    throws XMLStreamException;

  public abstract boolean hasNext();

  public abstract XMLEvent peek()
    throws XMLStreamException;

  public abstract String getElementText()
    throws XMLStreamException;

  public abstract XMLEvent nextTag()
    throws XMLStreamException;

  public abstract Object getProperty(String paramString)
    throws IllegalArgumentException;

  public abstract void close()
    throws XMLStreamException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.XMLEventReader
 * JD-Core Version:    0.6.2
 */