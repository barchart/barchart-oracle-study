package sun.plugin.dom.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLTableCaptionElement;
import org.w3c.dom.html.HTMLTableSectionElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.exception.PluginNotSupportedException;

public final class HTMLTableElement extends HTMLElement
  implements org.w3c.dom.html.HTMLTableElement
{
  public HTMLTableElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLTableCaptionElement getCaption()
  {
    Object localObject = this.obj.getMember("caption");
    if ((localObject != null) && ((localObject instanceof DOMObject)))
    {
      org.w3c.dom.html.HTMLElement localHTMLElement = DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
      if ((localHTMLElement != null) && ((localHTMLElement instanceof HTMLTableCaptionElement)))
        return (HTMLTableCaptionElement)localHTMLElement;
    }
    return null;
  }

  public void setCaption(HTMLTableCaptionElement paramHTMLTableCaptionElement)
  {
    if ((paramHTMLTableCaptionElement != null) && ((paramHTMLTableCaptionElement instanceof HTMLElement)))
    {
      DOMObject localDOMObject = ((HTMLElement)paramHTMLTableCaptionElement).getDOMObject();
      this.obj.setMember("caption", localDOMObject);
      return;
    }
    throw new PluginNotSupportedException("HTMLTableElement.setCaption() is not supported.");
  }

  public HTMLTableSectionElement getTHead()
  {
    Object localObject = this.obj.getMember("tHead");
    if ((localObject != null) && ((localObject instanceof DOMObject)))
    {
      org.w3c.dom.html.HTMLElement localHTMLElement = DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
      if ((localHTMLElement != null) && ((localHTMLElement instanceof HTMLTableSectionElement)))
        return (HTMLTableSectionElement)localHTMLElement;
    }
    return null;
  }

  public void setTHead(HTMLTableSectionElement paramHTMLTableSectionElement)
  {
    if ((paramHTMLTableSectionElement != null) && ((paramHTMLTableSectionElement instanceof HTMLElement)))
    {
      DOMObject localDOMObject = ((HTMLElement)paramHTMLTableSectionElement).getDOMObject();
      this.obj.setMember("tHead", localDOMObject);
      return;
    }
    throw new PluginNotSupportedException("HTMLTableElement.setTHread() is not supported.");
  }

  public HTMLTableSectionElement getTFoot()
  {
    Object localObject = this.obj.getMember("tFoot");
    if ((localObject != null) && ((localObject instanceof DOMObject)))
    {
      org.w3c.dom.html.HTMLElement localHTMLElement = DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
      if ((localHTMLElement != null) && ((localHTMLElement instanceof HTMLTableSectionElement)))
        return (HTMLTableSectionElement)localHTMLElement;
    }
    return null;
  }

  public void setTFoot(HTMLTableSectionElement paramHTMLTableSectionElement)
  {
    if ((paramHTMLTableSectionElement != null) && ((paramHTMLTableSectionElement instanceof HTMLElement)))
    {
      DOMObject localDOMObject = ((HTMLElement)paramHTMLTableSectionElement).getDOMObject();
      this.obj.setMember("tFoot", localDOMObject);
      return;
    }
    throw new PluginNotSupportedException("HTMLTableElement.setTFoot() is not supported.");
  }

  public HTMLCollection getRows()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("rows"), (HTMLDocument)getOwnerDocument());
  }

  public HTMLCollection getTBodies()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("tBodies"), (HTMLDocument)getOwnerDocument());
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

  public String getBorder()
  {
    return getAttribute("border");
  }

  public void setBorder(String paramString)
  {
    setAttribute("border", paramString);
  }

  public String getCellPadding()
  {
    return getAttribute("cellPadding");
  }

  public void setCellPadding(String paramString)
  {
    setAttribute("cellPadding", paramString);
  }

  public String getCellSpacing()
  {
    return getAttribute("cellSpacing");
  }

  public void setCellSpacing(String paramString)
  {
    setAttribute("cellSpacing", paramString);
  }

  public String getFrame()
  {
    return getAttribute("frame");
  }

  public void setFrame(String paramString)
  {
    setAttribute("frame", paramString);
  }

  public String getRules()
  {
    return getAttribute("rules");
  }

  public void setRules(String paramString)
  {
    setAttribute("rules", paramString);
  }

  public String getSummary()
  {
    return getAttribute("summary");
  }

  public void setSummary(String paramString)
  {
    setAttribute("summary", paramString);
  }

  public String getWidth()
  {
    return getAttribute("width");
  }

  public void setWidth(String paramString)
  {
    setAttribute("width", paramString);
  }

  public org.w3c.dom.html.HTMLElement createTHead()
  {
    Object localObject = this.obj.call("createTHead", null);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
    return null;
  }

  public void deleteTHead()
  {
    this.obj.call("deleteTHead", null);
  }

  public org.w3c.dom.html.HTMLElement createTFoot()
  {
    Object localObject = this.obj.call("createTFoot", null);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
    return null;
  }

  public void deleteTFoot()
  {
    this.obj.call("deleteTFoot", null);
  }

  public org.w3c.dom.html.HTMLElement createCaption()
  {
    Object localObject = this.obj.call("createCaption", null);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
    return null;
  }

  public void deleteCaption()
  {
    this.obj.call("deleteCaption", null);
  }

  public org.w3c.dom.html.HTMLElement insertRow(int paramInt)
    throws DOMException
  {
    Object localObject = this.obj.call("insertRow", new Object[] { new Integer(paramInt) });
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
    return null;
  }

  public void deleteRow(int paramInt)
    throws DOMException
  {
    this.obj.call("deleteRow", new Object[] { new Integer(paramInt) });
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLTableElement
 * JD-Core Version:    0.6.2
 */