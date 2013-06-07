package javax.xml.stream;

public abstract interface Location
{
  public abstract int getLineNumber();

  public abstract int getColumnNumber();

  public abstract int getCharacterOffset();

  public abstract String getPublicId();

  public abstract String getSystemId();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.Location
 * JD-Core Version:    0.6.2
 */