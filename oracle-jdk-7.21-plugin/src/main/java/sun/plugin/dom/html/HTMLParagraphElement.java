package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLParagraphElement extends HTMLElement
  implements org.w3c.dom.html.HTMLParagraphElement
{
  public HTMLParagraphElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getAlign()
  {
    return getAttribute("align");
  }

  public void setAlign(String paramString)
  {
    setAttribute("align", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLParagraphElement
 * JD-Core Version:    0.6.2
 */