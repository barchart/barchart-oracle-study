package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;

public abstract interface DOMImplementationCSS extends DOMImplementation
{
  public abstract CSSStyleSheet createCSSStyleSheet(String paramString1, String paramString2)
    throws DOMException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.css.DOMImplementationCSS
 * JD-Core Version:    0.6.2
 */