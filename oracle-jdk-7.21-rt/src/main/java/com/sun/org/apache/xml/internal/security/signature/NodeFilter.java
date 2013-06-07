package com.sun.org.apache.xml.internal.security.signature;

import org.w3c.dom.Node;

public abstract interface NodeFilter
{
  public abstract int isNodeInclude(Node paramNode);

  public abstract int isNodeIncludeDO(Node paramNode, int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.NodeFilter
 * JD-Core Version:    0.6.2
 */