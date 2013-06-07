package org.w3c.dom.css;

import org.w3c.dom.Element;
import org.w3c.dom.views.AbstractView;

public abstract interface ViewCSS extends AbstractView
{
  public abstract CSSStyleDeclaration getComputedStyle(Element paramElement, String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.css.ViewCSS
 * JD-Core Version:    0.6.2
 */