package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class HTMLLabelElement extends HTMLElement
  implements org.w3c.dom.html.HTMLLabelElement
{
  public HTMLLabelElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public String getHtmlFor()
  {
    return getAttribute("htmlFor");
  }

  public void setHtmlFor(String paramString)
  {
    setAttribute("htmlFor", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLLabelElement
 * JD-Core Version:    0.6.2
 */