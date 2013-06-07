package javax.xml.xpath;

import javax.xml.namespace.QName;

public abstract interface XPathFunctionResolver
{
  public abstract XPathFunction resolveFunction(QName paramQName, int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.xpath.XPathFunctionResolver
 * JD-Core Version:    0.6.2
 */