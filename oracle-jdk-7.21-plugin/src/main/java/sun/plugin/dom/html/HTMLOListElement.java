package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLOListElement extends HTMLElement
  implements org.w3c.dom.html.HTMLOListElement
{
  public HTMLOListElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public boolean getCompact()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "compact");
  }

  public void setCompact(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "compact", paramBoolean);
  }

  public int getStart()
  {
    return DOMObjectHelper.getIntMember(this.obj, "start");
  }

  public void setStart(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "start", paramInt);
  }

  public String getType()
  {
    return getAttribute("type");
  }

  public void setType(String paramString)
  {
    setAttribute("type", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLOListElement
 * JD-Core Version:    0.6.2
 */