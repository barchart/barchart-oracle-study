package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public abstract interface CSSPageRule extends CSSRule
{
  public abstract String getSelectorText();

  public abstract void setSelectorText(String paramString)
    throws DOMException;

  public abstract CSSStyleDeclaration getStyle();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.css.CSSPageRule
 * JD-Core Version:    0.6.2
 */