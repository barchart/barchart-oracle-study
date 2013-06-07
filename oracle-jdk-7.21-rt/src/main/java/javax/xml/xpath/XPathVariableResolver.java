package javax.xml.xpath;

import javax.xml.namespace.QName;

public abstract interface XPathVariableResolver
{
  public abstract Object resolveVariable(QName paramQName);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.xpath.XPathVariableResolver
 * JD-Core Version:    0.6.2
 */