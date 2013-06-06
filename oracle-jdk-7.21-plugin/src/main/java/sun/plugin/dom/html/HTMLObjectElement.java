package sun.plugin.dom.html;

import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLObjectElement extends HTMLElement
  implements org.w3c.dom.html.HTMLObjectElement
{
  public HTMLObjectElement(DOMObject paramDOMObject, org.w3c.dom.html.HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLFormElement getForm()
  {
    return DOMObjectFactory.createHTMLFormElement(this.obj.getMember("form"), (org.w3c.dom.html.HTMLDocument)getOwnerDocument());
  }

  public String getCode()
  {
    return getAttribute("code");
  }

  public void setCode(String paramString)
  {
    setAttribute("code", paramString);
  }

  public String getAlign()
  {
    return getAttribute("align");
  }

  public void setAlign(String paramString)
  {
    setAttribute("align", paramString);
  }

  public String getArchive()
  {
    return getAttribute("archive");
  }

  public void setArchive(String paramString)
  {
    setAttribute("archive", paramString);
  }

  public String getBorder()
  {
    return getAttribute("border");
  }

  public void setBorder(String paramString)
  {
    setAttribute("border", paramString);
  }

  public String getCodeBase()
  {
    return getAttribute("codeBase");
  }

  public void setCodeBase(String paramString)
  {
    setAttribute("codeBase", paramString);
  }

  public String getCodeType()
  {
    return getAttribute("codeType");
  }

  public void setCodeType(String paramString)
  {
    setAttribute("codeType", paramString);
  }

  public String getData()
  {
    return getAttribute("data");
  }

  public void setData(String paramString)
  {
    setAttribute("data", paramString);
  }

  public boolean getDeclare()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "declare");
  }

  public void setDeclare(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "declare", paramBoolean);
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

  public String getStandby()
  {
    return getAttribute("standby");
  }

  public void setStandby(String paramString)
  {
    setAttribute("standby", paramString);
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

  public void setType(String paramString)
  {
    setAttribute("type", paramString);
  }

  public String getUseMap()
  {
    return getAttribute("useMap");
  }

  public void setUseMap(String paramString)
  {
    setAttribute("useMap", paramString);
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

  public Document getContentDocument()
  {
    Object localObject = this.obj.getMember("contentDocument");
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new HTMLDocument((DOMObject)localObject, null);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLObjectElement
 * JD-Core Version:    0.6.2
 */