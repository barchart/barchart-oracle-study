package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLOptionElement extends HTMLElement
  implements org.w3c.dom.html.HTMLOptionElement
{
  public HTMLOptionElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLFormElement getForm()
  {
    return DOMObjectFactory.createHTMLFormElement(this.obj.getMember("form"), (HTMLDocument)getOwnerDocument());
  }

  public boolean getDefaultSelected()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "defaultSelected");
  }

  public void setDefaultSelected(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "defaultSelected", paramBoolean);
  }

  public String getText()
  {
    return getAttribute("text");
  }

  public int getIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "index");
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "disabled", paramBoolean);
  }

  public String getLabel()
  {
    return getAttribute("label");
  }

  public void setLabel(String paramString)
  {
    setAttribute("label", paramString);
  }

  public boolean getSelected()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "selected");
  }

  public void setSelected(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "selected", paramBoolean);
  }

  public String getValue()
  {
    return getAttribute("value");
  }

  public void setValue(String paramString)
  {
    setAttribute("value", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLOptionElement
 * JD-Core Version:    0.6.2
 */