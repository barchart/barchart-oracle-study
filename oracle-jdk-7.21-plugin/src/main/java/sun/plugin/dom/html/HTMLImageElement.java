package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLImageElement extends HTMLElement
  implements org.w3c.dom.html.HTMLImageElement
{
  public HTMLImageElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getLowSrc()
  {
    return getAttribute("lowSrc");
  }

  public void setLowSrc(String paramString)
  {
    setAttribute("lowSrc", paramString);
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public String getAlign()
  {
    return getAttribute("align");
  }

  public void setAlign(String paramString)
  {
    setAttribute("align", paramString);
  }

  public String getAlt()
  {
    return getAttribute("alt");
  }

  public void setAlt(String paramString)
  {
    setAttribute("alt", paramString);
  }

  public String getBorder()
  {
    return getAttribute("border");
  }

  public void setBorder(String paramString)
  {
    setAttribute("border", paramString);
  }

  public String getHeight()
  {
    return getAttribute("height");
  }

  public void setHeight(String paramString)
  {
    setAttribute("height", paramString);
  }

  public String getHspace()
  {
    return getAttribute("hspace");
  }

  public void setHspace(String paramString)
  {
    setAttribute("hspace", paramString);
  }

  public boolean getIsMap()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "isMap");
  }

  public void setIsMap(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "isMap", paramBoolean);
  }

  public String getLongDesc()
  {
    return getAttribute("longDesc");
  }

  public void setLongDesc(String paramString)
  {
    setAttribute("longDesc", paramString);
  }

  public String getSrc()
  {
    return getAttribute("src");
  }

  public void setSrc(String paramString)
  {
    setAttribute("src", paramString);
  }

  public String getUseMap()
  {
    return getAttribute("useMap");
  }

  public void setUseMap(String paramString)
  {
    setAttribute("useMap", paramString);
  }

  public String getVspace()
  {
    return getAttribute("vspace");
  }

  public void setVspace(String paramString)
  {
    setAttribute("vspace", paramString);
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
 * Qualified Name:     sun.plugin.dom.html.HTMLImageElement
 * JD-Core Version:    0.6.2
 */