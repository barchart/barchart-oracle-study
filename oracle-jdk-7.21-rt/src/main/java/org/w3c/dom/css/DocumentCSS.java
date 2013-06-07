package org.w3c.dom.css;

import org.w3c.dom.Element;
import org.w3c.dom.stylesheets.DocumentStyle;

public abstract interface DocumentCSS extends DocumentStyle
{
  public abstract CSSStyleDeclaration getOverrideStyle(Element paramElement, String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.css.DocumentCSS
 * JD-Core Version:    0.6.2
 */