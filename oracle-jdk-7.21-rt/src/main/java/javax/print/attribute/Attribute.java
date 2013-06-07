package javax.print.attribute;

import java.io.Serializable;

public abstract interface Attribute extends Serializable
{
  public abstract Class<? extends Attribute> getCategory();

  public abstract String getName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.attribute.Attribute
 * JD-Core Version:    0.6.2
 */