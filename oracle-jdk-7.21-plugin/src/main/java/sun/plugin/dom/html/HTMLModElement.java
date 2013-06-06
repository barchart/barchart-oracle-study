package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLModElement extends HTMLElement
  implements org.w3c.dom.html.HTMLModElement
{
  public HTMLModElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public String getDateTime()
  {
    return getAttribute("dateTime");
  }

  public void setDateTime(String paramString)
  {
    setAttribute("dateTime", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLModElement
 * JD-Core Version:    0.6.2
 */