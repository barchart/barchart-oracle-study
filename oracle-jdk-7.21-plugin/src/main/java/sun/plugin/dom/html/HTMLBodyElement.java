package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public class HTMLBodyElement extends HTMLElement
  implements org.w3c.dom.html.HTMLBodyElement
{
  public HTMLBodyElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getALink()
  {
    return getAttribute("aLink");
  }

  public void setALink(String paramString)
  {
    setAttribute("aLink", paramString);
  }

  public String getBackground()
  {
    return getAttribute("background");
  }

  public void setBackground(String paramString)
  {
    setAttribute("background", paramString);
  }

  public String getBgColor()
  {
    return getAttribute("bgColor");
  }

  public void setBgColor(String paramString)
  {
    setAttribute("bgColor", paramString);
  }

  public String getLink()
  {
    return getAttribute("link");
  }

  public void setLink(String paramString)
  {
    setAttribute("link", paramString);
  }

  public String getText()
  {
    return getAttribute("text");
  }

  public void setText(String paramString)
  {
    setAttribute("text", paramString);
  }

  public String getVLink()
  {
    return getAttribute("vLink");
  }

  public void setVLink(String paramString)
  {
    setAttribute("vLink", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLBodyElement
 * JD-Core Version:    0.6.2
 */