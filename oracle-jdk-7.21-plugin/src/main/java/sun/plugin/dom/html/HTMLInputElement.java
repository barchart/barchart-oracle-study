package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLInputElement extends HTMLElement
  implements org.w3c.dom.html.HTMLInputElement
{
  public HTMLInputElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
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

  public boolean getDefaultChecked()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "defaultChecked");
  }

  public void setDefaultChecked(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "defaultChecked", paramBoolean);
  }

  public HTMLFormElement getForm()
  {
    return DOMObjectFactory.createHTMLFormElement(this.obj.getMember("form"), (HTMLDocument)getOwnerDocument());
  }

  public String getAccept()
  {
    return getAttribute("accept");
  }

  public void setAccept(String paramString)
  {
    setAttribute("accept", paramString);
  }

  public String getAccessKey()
  {
    return getAttribute("accessKey");
  }

  public void setAccessKey(String paramString)
  {
    setAttribute("accessKey", paramString);
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

  public boolean getChecked()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "checked");
  }

  public void setChecked(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "checked", paramBoolean);
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "disabled", paramBoolean);
  }

  public int getMaxLength()
  {
    return DOMObjectHelper.getIntMember(this.obj, "maxLength");
  }

  public void setMaxLength(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "maxLength", paramInt);
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

  public String getSize()
  {
    return getAttribute("size");
  }

  public void setSize(String paramString)
  {
    setAttribute("size", paramString);
  }

  public String getSrc()
  {
    return getAttribute("src");
  }

  public void setSrc(String paramString)
  {
    setAttribute("src", paramString);
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

  public String getUseMap()
  {
    return getAttribute("useMap");
  }

  public void setUseMap(String paramString)
  {
    setAttribute("useMap", paramString);
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

  public void click()
  {
    this.obj.call("click", null);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLInputElement
 * JD-Core Version:    0.6.2
 */