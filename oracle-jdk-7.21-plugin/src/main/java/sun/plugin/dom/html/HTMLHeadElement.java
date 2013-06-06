package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLHeadElement extends HTMLElement
  implements org.w3c.dom.html.HTMLHeadElement
{
  public HTMLHeadElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getProfile()
  {
    return getAttribute("profile");
  }

  public void setProfile(String paramString)
  {
    setAttribute("profile", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLHeadElement
 * JD-Core Version:    0.6.2
 */