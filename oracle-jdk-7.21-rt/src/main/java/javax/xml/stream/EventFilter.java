package javax.xml.stream;

import javax.xml.stream.events.XMLEvent;

public abstract interface EventFilter
{
  public abstract boolean accept(XMLEvent paramXMLEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.EventFilter
 * JD-Core Version:    0.6.2
 */