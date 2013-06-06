package sun.plugin.dom.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLTableRowElement extends HTMLElement
  implements org.w3c.dom.html.HTMLTableRowElement
{
  public HTMLTableRowElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public int getRowIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "rowIndex");
  }

  public int getSectionRowIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "sectionRowIndex");
  }

  public HTMLCollection getCells()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("cells"), (HTMLDocument)getOwnerDocument());
  }

  public String getAlign()
  {
    return getAttribute("align");
  }

  public void setAlign(String paramString)
  {
    setAttribute("align", paramString);
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

  public String getVAlign()
  {
    return getAttribute("vAlign");
  }

  public void setVAlign(String paramString)
  {
    setAttribute("vAlign", paramString);
  }

  public org.w3c.dom.html.HTMLElement insertCell(int paramInt)
    throws DOMException
  {
    Object localObject = this.obj.call("insertCell", new Object[] { new Integer(paramInt) });
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
    return null;
  }

  public void deleteCell(int paramInt)
    throws DOMException
  {
    this.obj.call("deleteCell", new Object[] { new Integer(paramInt) });
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLTableRowElement
 * JD-Core Version:    0.6.2
 */