package sun.print;

import javax.print.attribute.PrintServiceAttributeSet;

abstract interface AttributeUpdater
{
  public abstract PrintServiceAttributeSet getUpdatedAttributes();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.AttributeUpdater
 * JD-Core Version:    0.6.2
 */