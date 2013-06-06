package sun.plugin.dom.css;

import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class CSSValueList extends CSSValue
  implements org.w3c.dom.css.CSSValueList
{
  public CSSValueList(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public int getLength()
  {
    return ((Number)this.obj.getMember("length")).intValue();
  }

  public org.w3c.dom.css.CSSValue item(int paramInt)
  {
    return DOMObjectFactory.createCSSValue(this.obj.call("item", new Object[] { new Integer(paramInt) }), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSValueList
 * JD-Core Version:    0.6.2
 */