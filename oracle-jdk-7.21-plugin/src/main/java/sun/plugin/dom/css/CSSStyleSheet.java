package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;
import sun.plugin.dom.stylesheets.StyleSheet;

public class CSSStyleSheet extends StyleSheet
  implements org.w3c.dom.css.CSSStyleSheet
{
  public CSSStyleSheet(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public CSSRule getOwnerRule()
  {
    return DOMObjectFactory.createCSSRule(this.obj.getMember("ownerRule"), this.doc);
  }

  public CSSRuleList getCssRules()
  {
    return DOMObjectFactory.createCSSRuleList(this.obj.getMember("cssRules"), this.doc);
  }

  public int insertRule(String paramString, int paramInt)
    throws DOMException
  {
    String str1 = null;
    try
    {
      paramString = paramString.trim();
      int i = paramString.indexOf('{');
      if ((i <= 0) || (!paramString.endsWith("}")))
        throw new IllegalArgumentException("Invalid Css text");
      String str2 = paramString.substring(0, i);
      paramString = paramString.substring(i + 1, paramString.length() - 1);
      str1 = DOMObjectHelper.callStringMethod(this.obj, "addRule", new Object[] { str2, paramString, new Integer(paramInt) });
    }
    catch (DOMException localDOMException)
    {
      str1 = DOMObjectHelper.callStringMethod(this.obj, "insertRule", new Object[] { paramString, new Integer(paramInt) });
    }
    if (str1 != null)
      return Integer.parseInt(str1);
    return 0;
  }

  public void deleteRule(int paramInt)
    throws DOMException
  {
    try
    {
      DOMObjectHelper.callStringMethod(this.obj, "removeRule", new Object[] { new Integer(paramInt) });
    }
    catch (DOMException localDOMException)
    {
      DOMObjectHelper.callStringMethod(this.obj, "deleteRule", new Object[] { new Integer(paramInt) });
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSStyleSheet
 * JD-Core Version:    0.6.2
 */