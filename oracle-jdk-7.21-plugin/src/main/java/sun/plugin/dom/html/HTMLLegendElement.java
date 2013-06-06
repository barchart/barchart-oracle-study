package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class HTMLLegendElement extends HTMLElement
  implements org.w3c.dom.html.HTMLLegendElement
{
  public HTMLLegendElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLFormElement getForm()
  {
    return DOMObjectFactory.createHTMLFormElement(this.obj.getMember("form"), (HTMLDocument)getOwnerDocument());
  }

  public String getAccessKey()
  {
    return getAttribute("accessKey");
  }

  public void setAccessKey(String paramString)
  {
    setAttribute("accessKey", paramString);
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
 * Qualified Name:     sun.plugin.dom.html.HTMLLegendElement
 * JD-Core Version:    0.6.2
 */