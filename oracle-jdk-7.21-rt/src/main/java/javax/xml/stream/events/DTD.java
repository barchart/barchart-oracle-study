package javax.xml.stream.events;

import java.util.List;

public abstract interface DTD extends XMLEvent
{
  public abstract String getDocumentTypeDeclaration();

  public abstract Object getProcessedDTD();

  public abstract List getNotations();

  public abstract List getEntities();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.events.DTD
 * JD-Core Version:    0.6.2
 */