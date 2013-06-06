package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLTextAreaElement extends HTMLElement
  implements org.w3c.dom.html.HTMLTextAreaElement
{
  public HTMLTextAreaElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getDefaultValue()
  {
    return getAttribute("defaultValue");
  }

  public void setDefaultValue(String paramString)
  {
    setAttribute("defaultValue", paramString);
  }

  public HTMLFormElement getForm()
  {
    return DOMObjectFactory.createHTMLFormElement(this.obj.getMember("form"), (HTMLDocument)getOwnerDocument());
  }

  public String getAccessKey()
  {
    return getAttribute("accessKey");
  }

  public void setAccessKey(String paramString)
  {
    setAttribute("accessKey", paramString);
  }

  public int getCols()
  {
    return DOMObjectHelper.getIntMember(this.obj, "cols");
  }

  public void setCols(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "cols", paramInt);
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "disabled", paramBoolean);
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public boolean getReadOnly()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "readOnly");
  }

  public void setReadOnly(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "readOnly", paramBoolean);
  }

  public int getRows()
  {
    return DOMObjectHelper.getIntMember(this.obj, "rows");
  }

  public void setRows(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "rows", paramInt);
  }

  public int getTabIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "tabIndex");
  }

  public void setTabIndex(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "tabIndex", paramInt);
  }

  public String getType()
  {
    return getAttribute("type");
  }

  public String getValue()
  {
    return getAttribute("value");
  }

  public void setValue(String paramString)
  {
    setAttribute("value", paramString);
  }

  public void blur()
  {
    this.obj.call("blur", null);
  }

  public void focus()
  {
    this.obj.call("focus", null);
  }

  public void select()
  {
    this.obj.call("select", null);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLTextAreaElement
 * JD-Core Version:    0.6.2
 */