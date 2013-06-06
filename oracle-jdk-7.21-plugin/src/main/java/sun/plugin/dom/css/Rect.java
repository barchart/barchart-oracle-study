package sun.plugin.dom.css;

import org.w3c.dom.Document;
import org.w3c.dom.css.CSSPrimitiveValue;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class Rect
  implements org.w3c.dom.css.Rect
{
  private DOMObject obj;
  private Document document;

  public Rect(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
  }

  public CSSPrimitiveValue getTop()
  {
    return DOMObjectFactory.createCSSPrimitiveValue(this.obj.getMember("top"), this.document);
  }

  public CSSPrimitiveValue getRight()
  {
    return DOMObjectFactory.createCSSPrimitiveValue(this.obj.getMember("right"), this.document);
  }

  public CSSPrimitiveValue getBottom()
  {
    return DOMObjectFactory.createCSSPrimitiveValue(this.obj.getMember("bottom"), this.document);
  }

  public CSSPrimitiveValue getLeft()
  {
    return DOMObjectFactory.createCSSPrimitiveValue(this.obj.getMember("left"), this.document);
  }

  public String toString()
  {
    return this.obj.toString();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.Rect
 * JD-Core Version:    0.6.2
 */