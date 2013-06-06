package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public class HTMLBaseElement extends HTMLElement
  implements org.w3c.dom.html.HTMLBaseElement
{
  public HTMLBaseElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getHref()
  {
    return getAttribute("href");
  }

  public void setHref(String paramString)
  {
    setAttribute("href", paramString);
  }

  public String getTarget()
  {
    return getAttribute("target");
  }

  public void setTarget(String paramString)
  {
    setAttribute("target", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLBaseElement
 * JD-Core Version:    0.6.2
 */