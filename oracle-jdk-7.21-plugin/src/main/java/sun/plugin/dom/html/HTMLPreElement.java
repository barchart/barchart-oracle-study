package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLPreElement extends HTMLElement
  implements org.w3c.dom.html.HTMLPreElement
{
  public HTMLPreElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public int getWidth()
  {
    return DOMObjectHelper.getIntMember(this.obj, "width");
  }

  public void setWidth(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "width", paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLPreElement
 * JD-Core Version:    0.6.2
 */