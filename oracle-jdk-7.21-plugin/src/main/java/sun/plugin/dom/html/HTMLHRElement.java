package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLHRElement extends HTMLElement
  implements org.w3c.dom.html.HTMLHRElement
{
  public HTMLHRElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getAlign()
  {
    return getAttribute("align");
  }

  public void setAlign(String paramString)
  {
    setAttribute("align", paramString);
  }

  public boolean getNoShade()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "noShade");
  }

  public void setNoShade(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "noShade", paramBoolean);
  }

  public String getSize()
  {
    return getAttribute("size");
  }

  public void setSize(String paramString)
  {
    setAttribute("size", paramString);
  }

  public String getWidth()
  {
    return getAttribute("width");
  }

  public void setWidth(String paramString)
  {
    setAttribute("width", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLHRElement
 * JD-Core Version:    0.6.2
 */