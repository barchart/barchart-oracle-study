package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLTitleElement extends HTMLElement
  implements org.w3c.dom.html.HTMLTitleElement
{
  public HTMLTitleElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getText()
  {
    return getAttribute("text");
  }

  public void setText(String paramString)
  {
    setAttribute("text", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLTitleElement
 * JD-Core Version:    0.6.2
 */