package sun.plugin.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.DOMImplementationCSS;
import org.w3c.dom.html.HTMLDOMImplementation;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.exception.PluginNotSupportedException;

public class DOMImplementation
  implements org.w3c.dom.DOMImplementation, HTMLDOMImplementation, DOMImplementationCSS
{
  private DOMObject obj;

  public DOMImplementation(DOMObject paramDOMObject)
  {
    this.obj = paramDOMObject;
  }

  public boolean hasFeature(String paramString1, String paramString2)
  {
    try
    {
      return ((Boolean)this.obj.call("hasFeature", new Object[] { paramString1, paramString2 })).booleanValue();
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  public DocumentType createDocumentType(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    throw new PluginNotSupportedException("DOMImplementation.createDocumentType() is not supported");
  }

  public Document createDocument(String paramString1, String paramString2, DocumentType paramDocumentType)
    throws DOMException
  {
    throw new PluginNotSupportedException("DOMImplementation.createDocument() is not supported");
  }

  public HTMLDocument createHTMLDocument(String paramString)
  {
    throw new PluginNotSupportedException("HTMLDOMImplementation.createHTMLDocument() is not supported");
  }

  public CSSStyleSheet createCSSStyleSheet(String paramString1, String paramString2)
    throws DOMException
  {
    throw new PluginNotSupportedException("DOMImplementationCSS.createCSSStyleSheet() is not supported");
  }

  public Object getFeature(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("DOMImplementation.getFeature() is not supported.");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMImplementation
 * JD-Core Version:    0.6.2
 */