package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLMenuElement extends HTMLElement
  implements org.w3c.dom.html.HTMLMenuElement
{
  public HTMLMenuElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLMenuElement
 * JD-Core Version:    0.6.2
 */