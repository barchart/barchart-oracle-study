package javax.xml.stream.events;

public abstract interface EntityDeclaration extends XMLEvent
{
  public abstract String getPublicId();

  public abstract String getSystemId();

  public abstract String getName();

  public abstract String getNotationName();

  public abstract String getReplacementText();

  public abstract String getBaseURI();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.events.EntityDeclaration
 * JD-Core Version:    0.6.2
 */