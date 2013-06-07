package javax.xml.stream.events;

public abstract interface EntityReference extends XMLEvent
{
  public abstract EntityDeclaration getDeclaration();

  public abstract String getName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.events.EntityReference
 * JD-Core Version:    0.6.2
 */