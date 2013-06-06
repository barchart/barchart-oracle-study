package sun.plugin.dom.html;

import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;

public final class HTMLIFrameElement extends HTMLElement
  implements org.w3c.dom.html.HTMLIFrameElement
{
  public HTMLIFrameElement(DOMObject paramDOMObject, org.w3c.dom.html.HTMLDocument paramHTMLDocument)
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

  public String getFrameBorder()
  {
    return getAttribute("frameBorder");
  }

  public void setFrameBorder(String paramString)
  {
    setAttribute("frameBorder", paramString);
  }

  public String getHeight()
  {
    return getAttribute("height");
  }

  public void setHeight(String paramString)
  {
    setAttribute("height", paramString);
  }

  public String getLongDesc()
  {
    return getAttribute("longDesc");
  }

  public void setLongDesc(String paramString)
  {
    setAttribute("longDesc", paramString);
  }

  public String getMarginHeight()
  {
    return getAttribute("marginHeight");
  }

  public void setMarginHeight(String paramString)
  {
    setAttribute("marginHeight", paramString);
  }

  public String getMarginWidth()
  {
    return getAttribute("marginWidth");
  }

  public void setMarginWidth(String paramString)
  {
    setAttribute("marginWidth", paramString);
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public String getScrolling()
  {
    return getAttribute("scrolling");
  }

  public void setScrolling(String paramString)
  {
    setAttribute("scrolling", paramString);
  }

  public String getSrc()
  {
    return getAttribute("src");
  }

  public void setSrc(String paramString)
  {
    setAttribute("src", paramString);
  }

  public String getWidth()
  {
    return getAttribute("width");
  }

  public void setWidth(String paramString)
  {
    setAttribute("width", paramString);
  }

  public Document getContentDocument()
  {
    Object localObject = this.obj.getMember("contentDocument");
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new HTMLDocument((DOMObject)localObject, null);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLIFrameElement
 * JD-Core Version:    0.6.2
 */