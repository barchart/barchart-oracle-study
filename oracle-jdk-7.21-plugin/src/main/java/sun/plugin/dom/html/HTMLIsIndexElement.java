package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class HTMLIsIndexElement extends HTMLElement
  implements org.w3c.dom.html.HTMLIsIndexElement
{
  public HTMLIsIndexElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLFormElement getForm()
  {
    return DOMObjectFactory.createHTMLFormElement(this.obj.getMember("form"), (HTMLDocument)getOwnerDocument());
  }

  public String getPrompt()
  {
    return getAttribute("prompt");
  }

  public void setPrompt(String paramString)
  {
    setAttribute("prompt", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLIsIndexElement
 * JD-Core Version:    0.6.2
 */