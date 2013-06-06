package sun.plugin.dom.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLStyleElement;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.stylesheets.StyleSheet;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLLinkElement extends HTMLElement
  implements org.w3c.dom.html.HTMLLinkElement, HTMLStyleElement, LinkStyle
{
  public HTMLLinkElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "disabled", paramBoolean);
  }

  public String getCharset()
  {
    return getAttribute("charset");
  }

  public void setCharset(String paramString)
  {
    setAttribute("charset", paramString);
  }

  public String getHref()
  {
    return getAttribute("href");
  }

  public void setHref(String paramString)
  {
    setAttribute("href", paramString);
  }

  public String getHreflang()
  {
    return getAttribute("hreflang");
  }

  public void setHreflang(String paramString)
  {
    setAttribute("hreflang", paramString);
  }

  public String getMedia()
  {
    return getAttribute("media");
  }

  public void setMedia(String paramString)
  {
    setAttribute("media", paramString);
  }

  public String getRel()
  {
    return getAttribute("rel");
  }

  public void setRel(String paramString)
  {
    setAttribute("rel", paramString);
  }

  public String getRev()
  {
    return getAttribute("rev");
  }

  public void setRev(String paramString)
  {
    setAttribute("rev", paramString);
  }

  public String getTarget()
  {
    return getAttribute("target");
  }

  public void setTarget(String paramString)
  {
    setAttribute("target", paramString);
  }

  public String getType()
  {
    return getAttribute("type");
  }

  public void setType(String paramString)
  {
    setAttribute("type", paramString);
  }

  public StyleSheet getSheet()
  {
    Object localObject = null;
    try
    {
      localObject = this.obj.getMember("stylesheet");
    }
    catch (DOMException localDOMException1)
    {
    }
    if (localObject == null)
      try
      {
        localObject = this.obj.getMember("sheet");
      }
      catch (DOMException localDOMException2)
      {
      }
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return DOMObjectFactory.createStyleSheet((DOMObject)localObject, getOwnerDocument());
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLLinkElement
 * JD-Core Version:    0.6.2
 */