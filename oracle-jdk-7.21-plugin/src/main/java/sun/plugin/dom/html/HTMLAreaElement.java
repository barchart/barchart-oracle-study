package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLAreaElement extends HTMLElement
  implements org.w3c.dom.html.HTMLAreaElement
{
  public HTMLAreaElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getAccessKey()
  {
    return getAttribute("accessKey");
  }

  public void setAccessKey(String paramString)
  {
    setAttribute("accessKey", paramString);
  }

  public String getAlt()
  {
    return getAttribute("alt");
  }

  public void setAlt(String paramString)
  {
    setAttribute("alt", paramString);
  }

  public String getCoords()
  {
    return getAttribute("coords");
  }

  public void setCoords(String paramString)
  {
    setAttribute("coords", paramString);
  }

  public String getHref()
  {
    return getAttribute("href");
  }

  public void setHref(String paramString)
  {
    setAttribute("href", paramString);
  }

  public boolean getNoHref()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "noHref");
  }

  public void setNoHref(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "noHref", paramBoolean);
  }

  public String getShape()
  {
    return getAttribute("shape");
  }

  public void setShape(String paramString)
  {
    setAttribute("shape", paramString);
  }

  public int getTabIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "tabIndex");
  }

  public void setTabIndex(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "tabIndex", paramInt);
  }

  public String getTarget()
  {
    return getAttribute("target");
  }

  public void setTarget(String paramString)
  {
    setAttribute("target", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLAreaElement
 * JD-Core Version:    0.6.2
 */