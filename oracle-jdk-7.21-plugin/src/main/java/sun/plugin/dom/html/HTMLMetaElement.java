package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLMetaElement extends HTMLElement
  implements org.w3c.dom.html.HTMLMetaElement
{
  public HTMLMetaElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getContent()
  {
    return getAttribute("content");
  }

  public void setContent(String paramString)
  {
    setAttribute("content", paramString);
  }

  public String getHttpEquiv()
  {
    return getAttribute("httpEquiv");
  }

  public void setHttpEquiv(String paramString)
  {
    setAttribute("httpEquiv", paramString);
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public String getScheme()
  {
    return getAttribute("scheme");
  }

  public void setScheme(String paramString)
  {
    setAttribute("scheme", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLMetaElement
 * JD-Core Version:    0.6.2
 */