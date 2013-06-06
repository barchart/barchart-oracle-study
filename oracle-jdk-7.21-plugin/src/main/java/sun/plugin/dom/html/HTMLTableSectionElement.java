package sun.plugin.dom.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class HTMLTableSectionElement extends HTMLElement
  implements org.w3c.dom.html.HTMLTableSectionElement
{
  public HTMLTableSectionElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public HTMLCollection getRows()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("rows"), (HTMLDocument)getOwnerDocument());
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
 * Qualified Name:     sun.plugin.dom.html.HTMLTableSectionElement
 * JD-Core Version:    0.6.2
 */