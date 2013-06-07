package org.w3c.dom.xpath;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract interface XPathNamespace extends Node
{
  public static final short XPATH_NAMESPACE_NODE = 13;

  public abstract Element getOwnerElement();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.xpath.XPathNamespace
 * JD-Core Version:    0.6.2
 */