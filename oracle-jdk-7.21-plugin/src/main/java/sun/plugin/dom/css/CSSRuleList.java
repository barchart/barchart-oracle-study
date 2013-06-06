package sun.plugin.dom.css;

import org.w3c.dom.Document;
import org.w3c.dom.css.CSSRule;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSRuleList
  implements org.w3c.dom.css.CSSRuleList
{
  private DOMObject obj;
  private Document document;

  public CSSRuleList(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMemberNoEx(this.obj, "length");
  }

  public CSSRule item(int paramInt)
  {
    return DOMObjectFactory.createCSSRule(this.obj.getSlot(paramInt), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSRuleList
 * JD-Core Version:    0.6.2
 */