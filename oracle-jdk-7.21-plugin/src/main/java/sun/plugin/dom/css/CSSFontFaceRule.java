package sun.plugin.dom.css;

import org.w3c.dom.Document;
import org.w3c.dom.css.CSSStyleDeclaration;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class CSSFontFaceRule extends CSSRule
  implements org.w3c.dom.css.CSSFontFaceRule
{
  public CSSFontFaceRule(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public CSSStyleDeclaration getStyle()
  {
    return DOMObjectFactory.createCSSStyleDeclaration(this.obj.getMember("style"), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSFontFaceRule
 * JD-Core Version:    0.6.2
 */