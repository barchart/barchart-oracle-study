package sun.plugin.dom.css;

import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public class CSSValue
  implements org.w3c.dom.css.CSSValue
{
  protected DOMObject obj;
  protected Document document;

  public CSSValue(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
  }

  public String getCssText()
  {
    return DOMObjectHelper.getStringMember(this.obj, "cssText");
  }

  public void setCssText(String paramString)
  {
    this.obj.setMember("cssText", paramString);
  }

  public short getCssValueType()
  {
    return ((Number)this.obj.getMember("cssValueType")).shortValue();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSValue
 * JD-Core Version:    0.6.2
 */