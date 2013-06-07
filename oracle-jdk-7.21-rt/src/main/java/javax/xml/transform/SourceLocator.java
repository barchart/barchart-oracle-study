package javax.xml.transform;

public abstract interface SourceLocator
{
  public abstract String getPublicId();

  public abstract String getSystemId();

  public abstract int getLineNumber();

  public abstract int getColumnNumber();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.transform.SourceLocator
 * JD-Core Version:    0.6.2
 */