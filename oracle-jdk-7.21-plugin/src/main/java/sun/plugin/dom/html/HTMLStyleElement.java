package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLStyleElement extends HTMLElement
  implements org.w3c.dom.html.HTMLStyleElement
{
  public HTMLStyleElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public String getMedia()
  {
    return getAttribute("media");
  }

  public void setMedia(String paramString)
  {
    setAttribute("media", paramString);
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
 * Qualified Name:     sun.plugin.dom.html.HTMLStyleElement
 * JD-Core Version:    0.6.2
 */