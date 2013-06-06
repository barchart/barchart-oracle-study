package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLFrameSetElement extends HTMLElement
  implements org.w3c.dom.html.HTMLFrameSetElement
{
  public HTMLFrameSetElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getCols()
  {
    return getAttribute("cols");
  }

  public void setCols(String paramString)
  {
    setAttribute("cols", paramString);
  }

  public String getRows()
  {
    return getAttribute("rows");
  }

  public void setRows(String paramString)
  {
    setAttribute("rows", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLFrameSetElement
 * JD-Core Version:    0.6.2
 */