package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.css.CSSStyleSheet;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class CSSRule
  implements org.w3c.dom.css.CSSRule
{
  protected DOMObject obj;
  protected Document document;

  public CSSRule(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
  }

  public short getType()
  {
    return 0;
  }

  public String getCssText()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "cssText");
  }

  public void setCssText(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, "cssText", paramString);
  }

  public CSSStyleSheet getParentStyleSheet()
  {
    return DOMObjectFactory.createCSSStyleSheet(this.obj.getMember("parentStyleSheet"), this.document);
  }

  public org.w3c.dom.css.CSSRule getParentRule()
  {
    return DOMObjectFactory.createCSSRule(this.obj.getMember("parentRule"), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSRule
 * JD-Core Version:    0.6.2
 */