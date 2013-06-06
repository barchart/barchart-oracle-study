package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public class HTMLBRElement extends HTMLElement
  implements org.w3c.dom.html.HTMLBRElement
{
  public HTMLBRElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getClear()
  {
    return getAttribute("clear");
  }

  public void setClear(String paramString)
  {
    setAttribute("clear", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLBRElement
 * JD-Core Version:    0.6.2
 */