package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLTableCellElement extends HTMLElement
  implements org.w3c.dom.html.HTMLTableCellElement
{
  public HTMLTableCellElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public int getCellIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "cellIndex");
  }

  public String getAbbr()
  {
    return getAttribute("abbr");
  }

  public void setAbbr(String paramString)
  {
    setAttribute("abbr", paramString);
  }

  public String getAlign()
  {
    return getAttribute("align");
  }

  public void setAlign(String paramString)
  {
    setAttribute("align", paramString);
  }

  public String getAxis()
  {
    return getAttribute("axis");
  }

  public void setAxis(String paramString)
  {
    setAttribute("axis", paramString);
  }

  public String getBgColor()
  {
    return getAttribute("bgColor");
  }

  public void setBgColor(String paramString)
  {
    setAttribute("bgColor", paramString);
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

  public int getColSpan()
  {
    return DOMObjectHelper.getIntMember(this.obj, "colSpan");
  }

  public void setColSpan(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "colSpan", paramInt);
  }

  public String getHeaders()
  {
    return getAttribute("headers");
  }

  public void setHeaders(String paramString)
  {
    setAttribute("headers", paramString);
  }

  public String getHeight()
  {
    return getAttribute("height");
  }

  public void setHeight(String paramString)
  {
    setAttribute("height", paramString);
  }

  public boolean getNoWrap()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "noWrap");
  }

  public void setNoWrap(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "noWrap", paramBoolean);
  }

  public int getRowSpan()
  {
    return DOMObjectHelper.getIntMember(this.obj, "rowSpan");
  }

  public void setRowSpan(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "rowSpan", paramInt);
  }

  public String getScope()
  {
    return getAttribute("scope");
  }

  public void setScope(String paramString)
  {
    setAttribute("scope", paramString);
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
 * Qualified Name:     sun.plugin.dom.html.HTMLTableCellElement
 * JD-Core Version:    0.6.2
 */