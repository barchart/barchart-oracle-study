package sun.plugin.dom.css;

import org.w3c.dom.Document;
import org.w3c.dom.css.CSSPrimitiveValue;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class RGBColor
  implements org.w3c.dom.css.RGBColor
{
  private DOMObject obj;
  private Document document;

  public RGBColor(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
  }

  public CSSPrimitiveValue getRed()
  {
    return DOMObjectFactory.createCSSPrimitiveValue(this.obj.getMember("red"), this.document);
  }

  public CSSPrimitiveValue getGreen()
  {
    return DOMObjectFactory.createCSSPrimitiveValue(this.obj.getMember("green"), this.document);
  }

  public CSSPrimitiveValue getBlue()
  {
    return DOMObjectFactory.createCSSPrimitiveValue(this.obj.getMember("blue"), this.document);
  }

  public String toString()
  {
    return this.obj.toString();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.RGBColor
 * JD-Core Version:    0.6.2
 */