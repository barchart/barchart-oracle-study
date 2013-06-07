package javax.xml.xpath;

import java.util.List;

public abstract interface XPathFunction
{
  public abstract Object evaluate(List paramList)
    throws XPathFunctionException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.xpath.XPathFunction
 * JD-Core Version:    0.6.2
 */