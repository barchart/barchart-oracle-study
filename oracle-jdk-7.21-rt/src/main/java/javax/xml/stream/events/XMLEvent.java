package javax.xml.stream.events;

import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

public abstract interface XMLEvent extends XMLStreamConstants
{
  public abstract int getEventType();

  public abstract Location getLocation();

  public abstract boolean isStartElement();

  public abstract boolean isAttribute();

  public abstract boolean isNamespace();

  public abstract boolean isEndElement();

  public abstract boolean isEntityReference();

  public abstract boolean isProcessingInstruction();

  public abstract boolean isCharacters();

  public abstract boolean isStartDocument();

  public abstract boolean isEndDocument();

  public abstract StartElement asStartElement();

  public abstract EndElement asEndElement();

  public abstract Characters asCharacters();

  public abstract QName getSchemaType();

  public abstract void writeAsEncodedUnicode(Writer paramWriter)
    throws XMLStreamException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.events.XMLEvent
 * JD-Core Version:    0.6.2
 */