package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLAppletElement extends HTMLElement
  implements org.w3c.dom.html.HTMLAppletElement
{
  public HTMLAppletElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public String getAlt()
  {
    return getAttribute("alt");
  }

  public void setAlt(String paramString)
  {
    setAttribute("alt", paramString);
  }

  public String getArchive()
  {
    return getAttribute("archive");
  }

  public void setArchive(String paramString)
  {
    setAttribute("archive", paramString);
  }

  public String getCode()
  {
    return getAttribute("code");
  }

  public void setCode(String paramString)
  {
    setAttribute("code", paramString);
  }

  public String getCodeBase()
  {
    return getAttribute("codeBase");
  }

  public void setCodeBase(String paramString)
  {
    setAttribute("codeBase", paramString);
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

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public String getObject()
  {
    return getAttribute("object");
  }

  public void setObject(String paramString)
  {
    setAttribute("object", paramString);
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
 * Qualified Name:     sun.plugin.dom.html.HTMLAppletElement
 * JD-Core Version:    0.6.2
 */