package javax.xml.stream.events;

public abstract interface StartDocument extends XMLEvent
{
  public abstract String getSystemId();

  public abstract String getCharacterEncodingScheme();

  public abstract boolean encodingSet();

  public abstract boolean isStandalone();

  public abstract boolean standaloneSet();

  public abstract String getVersion();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.events.StartDocument
 * JD-Core Version:    0.6.2
 */