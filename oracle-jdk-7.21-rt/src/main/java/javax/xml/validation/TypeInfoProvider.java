package javax.xml.validation;

import org.w3c.dom.TypeInfo;

public abstract class TypeInfoProvider
{
  public abstract TypeInfo getElementTypeInfo();

  public abstract TypeInfo getAttributeTypeInfo(int paramInt);

  public abstract boolean isIdAttribute(int paramInt);

  public abstract boolean isSpecified(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.validation.TypeInfoProvider
 * JD-Core Version:    0.6.2
 */