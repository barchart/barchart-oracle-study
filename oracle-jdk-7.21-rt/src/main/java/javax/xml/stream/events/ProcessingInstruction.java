package javax.xml.stream.events;

public abstract interface ProcessingInstruction extends XMLEvent
{
  public abstract String getTarget();

  public abstract String getData();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.events.ProcessingInstruction
 * JD-Core Version:    0.6.2
 */