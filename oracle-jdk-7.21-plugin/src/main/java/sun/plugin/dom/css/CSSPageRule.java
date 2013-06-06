package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.css.CSSStyleDeclaration;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSPageRule extends CSSRule
  implements org.w3c.dom.css.CSSPageRule
{
  public CSSPageRule(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getSelectorText()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "selectorText");
  }

  public void setSelectorText(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMemberNoEx(this.obj, "selectorText", paramString);
  }

  public CSSStyleDeclaration getStyle()
  {
    return DOMObjectFactory.createCSSStyleDeclaration(this.obj.getMember("style"), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSPageRule
 * JD-Core Version:    0.6.2
 */