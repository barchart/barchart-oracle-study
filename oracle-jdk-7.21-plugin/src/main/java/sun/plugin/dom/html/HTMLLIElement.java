package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLLIElement extends HTMLElement
  implements org.w3c.dom.html.HTMLLIElement
{
  public HTMLLIElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getType()
  {
    return getAttribute("type");
  }

  public void setType(String paramString)
  {
    setAttribute("type", paramString);
  }

  public int getValue()
  {
    return DOMObjectHelper.getIntMember(this.obj, "value");
  }

  public void setValue(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "value", paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLLIElement
 * JD-Core Version:    0.6.2
 */