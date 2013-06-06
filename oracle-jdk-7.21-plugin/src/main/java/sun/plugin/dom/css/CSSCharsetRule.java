package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSCharsetRule extends CSSRule
  implements org.w3c.dom.css.CSSCharsetRule
{
  public CSSCharsetRule(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public short getType()
  {
    return 2;
  }

  public String getEncoding()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "encoding");
  }

  public void setEncoding(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMemberNoEx(this.obj, "encoding", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSCharsetRule
 * JD-Core Version:    0.6.2
 */