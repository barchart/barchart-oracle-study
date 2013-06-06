package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLParamElement extends HTMLElement
  implements org.w3c.dom.html.HTMLParamElement
{
  public HTMLParamElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public String getType()
  {
    return getAttribute("type");
  }

  public void setType(String paramString)
  {
    setAttribute("type", paramString);
  }

  public String getValue()
  {
    return getAttribute("value");
  }

  public void setValue(String paramString)
  {
    setAttribute("value", paramString);
  }

  public String getValueType()
  {
    return getAttribute("valueType");
  }

  public void setValueType(String paramString)
  {
    setAttribute("valueType", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLParamElement
 * JD-Core Version:    0.6.2
 */