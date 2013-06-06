package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLAnchorElement extends HTMLElement
  implements org.w3c.dom.html.HTMLAnchorElement
{
  public HTMLAnchorElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public String getCharset()
  {
    return getAttribute("charset");
  }

  public void setCharset(String paramString)
  {
    setAttribute("charset", paramString);
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

  public String getHreflang()
  {
    return getAttribute("hreflang");
  }

  public void setHreflang(String paramString)
  {
    setAttribute("hreflang", paramString);
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public String getRel()
  {
    return getAttribute("rel");
  }

  public void setRel(String paramString)
  {
    setAttribute("rel", paramString);
  }

  public String getRev()
  {
    return getAttribute("rev");
  }

  public void setRev(String paramString)
  {
    setAttribute("rev", paramString);
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

  public String getType()
  {
    return getAttribute("type");
  }

  public void setType(String paramString)
  {
    setAttribute("type", paramString);
  }

  public void blur()
  {
    this.obj.call("blur", new Object[0]);
  }

  public void focus()
  {
    this.obj.call("focus", new Object[0]);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLAnchorElement
 * JD-Core Version:    0.6.2
 */