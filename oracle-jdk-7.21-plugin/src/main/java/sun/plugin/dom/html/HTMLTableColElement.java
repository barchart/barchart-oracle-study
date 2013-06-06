package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLTableColElement extends HTMLElement
  implements org.w3c.dom.html.HTMLTableColElement
{
  public HTMLTableColElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public String getCh()
  {
    return getAttribute("ch");
  }

  public void setCh(String paramString)
  {
    setAttribute("ch", paramString);
  }

  public String getChOff()
  {
    return getAttribute("chOff");
  }

  public void setChOff(String paramString)
  {
    setAttribute("chOff", paramString);
  }

  public int getSpan()
  {
    return DOMObjectHelper.getIntMember(this.obj, "span");
  }

  public void setSpan(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "span", paramInt);
  }

  public String getVAlign()
  {
    return getAttribute("vAlign");
  }

  public void setVAlign(String paramString)
  {
    setAttribute("vAlign", paramString);
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
 * Qualified Name:     sun.plugin.dom.html.HTMLTableColElement
 * JD-Core Version:    0.6.2
 */