package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSImportRule extends CSSRule
  implements org.w3c.dom.css.CSSImportRule
{
  public CSSImportRule(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public short getType()
  {
    return 3;
  }

  public String getHref()
  {
    return DOMObjectHelper.getStringMember(this.obj, "href");
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

  public CSSStyleSheet getStyleSheet()
  {
    return DOMObjectFactory.createCSSStyleSheet(this.obj.getMember("styleSheet"), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSImportRule
 * JD-Core Version:    0.6.2
 */