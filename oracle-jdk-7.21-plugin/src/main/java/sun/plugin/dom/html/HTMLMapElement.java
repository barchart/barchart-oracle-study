package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class HTMLMapElement extends HTMLElement
  implements org.w3c.dom.html.HTMLMapElement
{
  public HTMLMapElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLCollection getAreas()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("areas"), (HTMLDocument)getOwnerDocument());
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLMapElement
 * JD-Core Version:    0.6.2
 */