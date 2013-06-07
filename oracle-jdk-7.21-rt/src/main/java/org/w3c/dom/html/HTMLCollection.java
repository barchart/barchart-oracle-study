package org.w3c.dom.html;

import org.w3c.dom.Node;

public abstract interface HTMLCollection
{
  public abstract int getLength();

  public abstract Node item(int paramInt);

  public abstract Node namedItem(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.html.HTMLCollection
 * JD-Core Version:    0.6.2
 */