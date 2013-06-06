package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLQuoteElement extends HTMLElement
  implements org.w3c.dom.html.HTMLQuoteElement
{
  public HTMLQuoteElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getCite()
  {
    return getAttribute("cite");
  }

  public void setCite(String paramString)
  {
    setAttribute("cite", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLQuoteElement
 * JD-Core Version:    0.6.2
 */