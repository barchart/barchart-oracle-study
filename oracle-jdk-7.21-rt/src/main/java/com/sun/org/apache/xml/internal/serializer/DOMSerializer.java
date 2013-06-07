package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import org.w3c.dom.Node;

public abstract interface DOMSerializer
{
  public abstract void serialize(Node paramNode)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.serializer.DOMSerializer
 * JD-Core Version:    0.6.2
 */