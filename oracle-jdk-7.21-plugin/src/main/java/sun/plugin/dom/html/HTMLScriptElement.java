package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLScriptElement extends HTMLElement
  implements org.w3c.dom.html.HTMLScriptElement
{
  public HTMLScriptElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getText()
  {
    return getAttribute("text");
  }

  public void setText(String paramString)
  {
    setAttribute("text", paramString);
  }

  public String getHtmlFor()
  {
    return getAttribute("htmlFor");
  }

  public void setHtmlFor(String paramString)
  {
    setAttribute("htmlFor", paramString);
  }

  public String getEvent()
  {
    return getAttribute("event");
  }

  public void setEvent(String paramString)
  {
    setAttribute("event", paramString);
  }

  public String getCharset()
  {
    return getAttribute("charset");
  }

  public void setCharset(String paramString)
  {
    setAttribute("charset", paramString);
  }

  public boolean getDefer()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "defer");
  }

  public void setDefer(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "defer", paramBoolean);
  }

  public String getSrc()
  {
    return getAttribute("src");
  }

  public void setSrc(String paramString)
  {
    setAttribute("src", paramString);
  }

  public String getType()
  {
    return getAttribute("type");
  }

  public void setType(String paramString)
  {
    setAttribute("type", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLScriptElement
 * JD-Core Version:    0.6.2
 */