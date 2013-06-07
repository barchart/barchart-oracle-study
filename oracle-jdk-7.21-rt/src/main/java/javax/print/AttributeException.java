package javax.print;

import javax.print.attribute.Attribute;

public abstract interface AttributeException
{
  public abstract Class[] getUnsupportedAttributes();

  public abstract Attribute[] getUnsupportedValues();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.AttributeException
 * JD-Core Version:    0.6.2
 */