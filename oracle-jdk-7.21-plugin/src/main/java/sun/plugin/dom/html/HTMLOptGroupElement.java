package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLOptGroupElement extends HTMLElement
  implements org.w3c.dom.html.HTMLOptGroupElement
{
  public HTMLOptGroupElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "disabled", paramBoolean);
  }

  public String getLabel()
  {
    return getAttribute("label");
  }

  public void setLabel(String paramString)
  {
    setAttribute("label", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLOptGroupElement
 * JD-Core Version:    0.6.2
 */