package org.xml.sax;

public abstract interface Locator
{
  public abstract String getPublicId();

  public abstract String getSystemId();

  public abstract int getLineNumber();

  public abstract int getColumnNumber();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.xml.sax.Locator
 * JD-Core Version:    0.6.2
 */