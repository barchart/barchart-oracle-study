package sun.plugin.dom.html;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.core.Element;

public class HTMLElement extends Element
  implements org.w3c.dom.html.HTMLElement, ElementCSSInlineStyle
{
  public HTMLElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getId()
  {
    return getAttribute("id");
  }

  public void setId(String paramString)
  {
    setAttribute("id", paramString);
  }

  public String getTitle()
  {
    return getAttribute("title");
  }

  public void setTitle(String paramString)
  {
    setAttribute("title", paramString);
  }

  public String getLang()
  {
    return getAttribute("lang");
  }

  public void setLang(String paramString)
  {
    setAttribute("lang", paramString);
  }

  public String getDir()
  {
    return getAttribute("dir");
  }

  public void setDir(String paramString)
  {
    setAttribute("dir", paramString);
  }

  public String getClassName()
  {
    return getAttribute("className");
  }

  public void setClassName(String paramString)
  {
    setAttribute("className", paramString);
  }

  public CSSStyleDeclaration getStyle()
  {
    return DOMObjectFactory.createCSSStyleDeclaration(this.obj.getMember("style"), getOwnerDocument());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLElement
 * JD-Core Version:    0.6.2
 */