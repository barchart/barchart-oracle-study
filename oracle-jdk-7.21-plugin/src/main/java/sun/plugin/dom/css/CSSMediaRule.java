package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.stylesheets.MediaList;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSMediaRule extends CSSRule
  implements org.w3c.dom.css.CSSMediaRule
{
  public CSSMediaRule(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public MediaList getMedia()
  {
    try
    {
      return DOMObjectFactory.createMediaList(this.obj.getMember("media"), this.document);
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  public CSSRuleList getCssRules()
  {
    return DOMObjectFactory.createCSSRuleList(this.obj.getMember("cssRules"), this.document);
  }

  public int insertRule(String paramString, int paramInt)
    throws DOMException
  {
    String str = null;
    try
    {
      str = DOMObjectHelper.callStringMethod(this.obj, "addRule", new Object[] { new Integer(paramInt) });
    }
    catch (DOMException localDOMException)
    {
      str = DOMObjectHelper.callStringMethod(this.obj, "insertRule", new Object[] { new Integer(paramInt) });
    }
    if (str != null)
      return Integer.parseInt(str);
    return 0;
  }

  public void deleteRule(int paramInt)
    throws DOMException
  {
    try
    {
      this.obj.call("removeRule", new Object[] { new Integer(paramInt) });
    }
    catch (DOMException localDOMException)
    {
      this.obj.call("deleteRule", new Object[] { new Integer(paramInt) });
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSMediaRule
 * JD-Core Version:    0.6.2
 */