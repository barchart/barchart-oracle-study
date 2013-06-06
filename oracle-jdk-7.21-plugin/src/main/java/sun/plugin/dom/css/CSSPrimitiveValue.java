package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class CSSPrimitiveValue extends CSSValue
  implements org.w3c.dom.css.CSSPrimitiveValue
{
  public CSSPrimitiveValue(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public short getPrimitiveType()
  {
    return ((Number)this.obj.getMember("primitiveType")).shortValue();
  }

  public void setFloatValue(short paramShort, float paramFloat)
    throws DOMException
  {
    this.obj.call("setFloatValue", new Object[] { new Integer(paramShort), new Float(paramFloat) });
  }

  public float getFloatValue(short paramShort)
    throws DOMException
  {
    return ((Number)this.obj.call("getFloatValue", new Object[] { new Integer(paramShort) })).floatValue();
  }

  public void setStringValue(short paramShort, String paramString)
    throws DOMException
  {
    this.obj.call("setFloatValue", new Object[] { new Integer(paramShort), paramString });
  }

  public String getStringValue()
    throws DOMException
  {
    return (String)this.obj.call("getStringValue", null);
  }

  public Counter getCounterValue()
    throws DOMException
  {
    return DOMObjectFactory.createCSSCounter(this.obj.call("getCounterValue", null));
  }

  public Rect getRectValue()
    throws DOMException
  {
    return DOMObjectFactory.createCSSRect(this.obj.call("getRectValue", null), this.document);
  }

  public RGBColor getRGBColorValue()
    throws DOMException
  {
    return DOMObjectFactory.createCSSRGBColor(this.obj.call("getRGBColorValue", null), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSPrimitiveValue
 * JD-Core Version:    0.6.2
 */