package org.w3c.dom.ls;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract interface LSParserFilter
{
  public static final short FILTER_ACCEPT = 1;
  public static final short FILTER_REJECT = 2;
  public static final short FILTER_SKIP = 3;
  public static final short FILTER_INTERRUPT = 4;

  public abstract short startElement(Element paramElement);

  public abstract short acceptNode(Node paramNode);

  public abstract int getWhatToShow();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.ls.LSParserFilter
 * JD-Core Version:    0.6.2
 */