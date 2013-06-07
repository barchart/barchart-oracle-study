package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public abstract interface CSSStyleRule extends CSSRule
{
  public abstract String getSelectorText();

  public abstract void setSelectorText(String paramString)
    throws DOMException;

  public abstract CSSStyleDeclaration getStyle();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.css.CSSStyleRule
 * JD-Core Version:    0.6.2
 */