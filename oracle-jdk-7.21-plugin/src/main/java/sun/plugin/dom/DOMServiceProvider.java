package sun.plugin.dom;

import com.sun.java.browser.dom.DOMUnsupportedException;
import java.applet.Applet;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.css.CSSStyleSheet;
import sun.plugin.dom.exception.PluginNotSupportedException;

public class DOMServiceProvider extends com.sun.java.browser.dom.DOMServiceProvider
{
  public boolean canHandle(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof Applet));
  }

  public Document getDocument(Object paramObject)
    throws DOMUnsupportedException
  {
    try
    {
      if (canHandle(paramObject))
      {
        JSObject localJSObject1 = JSObject.getWindow((Applet)paramObject);
        if (localJSObject1 == null)
          throw new JSException("Unable to obtain Window object");
        JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
        if (localJSObject2 == null)
          throw new JSException("Unable to obtain Document object");
        return new sun.plugin.dom.html.HTMLDocument(new DOMObject(localJSObject2), null);
      }
    }
    catch (JSException localJSException)
    {
      localJSException.printStackTrace();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    throw new DOMUnsupportedException();
  }

  public DOMImplementation getDOMImplementation()
  {
    return new DOMImplementation()
    {
      public boolean hasFeature(String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousString1 == null)
          return false;
        if (paramAnonymousString2 == null)
          paramAnonymousString2 = "2.0";
        return (paramAnonymousString2.equals("2.0")) && ((paramAnonymousString1.equalsIgnoreCase("dom")) || (paramAnonymousString1.equalsIgnoreCase("xml")) || (paramAnonymousString1.equalsIgnoreCase("html")) || (paramAnonymousString1.equalsIgnoreCase("stylesheets")) || (paramAnonymousString1.equalsIgnoreCase("views")) || (paramAnonymousString1.equalsIgnoreCase("css")));
      }

      public DocumentType createDocumentType(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws DOMException
      {
        throw new PluginNotSupportedException("DOMImplementation.createDocumentType() is not supported");
      }

      public Document createDocument(String paramAnonymousString1, String paramAnonymousString2, DocumentType paramAnonymousDocumentType)
        throws DOMException
      {
        throw new PluginNotSupportedException("DOMImplementation.createDocument() is not supported");
      }

      public org.w3c.dom.html.HTMLDocument createHTMLDocument(String paramAnonymousString)
      {
        throw new PluginNotSupportedException("HTMLDOMImplementation.createHTMLDocument() is not supported");
      }

      public CSSStyleSheet createCSSStyleSheet(String paramAnonymousString1, String paramAnonymousString2)
        throws DOMException
      {
        throw new PluginNotSupportedException("DOMImplementationCSS.createCSSStyleSheet() is not supported");
      }

      public Object getFeature(String paramAnonymousString1, String paramAnonymousString2)
      {
        throw new PluginNotSupportedException("DOMImplementation.getFeature() is not supported.");
      }
    };
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMServiceProvider
 * JD-Core Version:    0.6.2
 */