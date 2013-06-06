package sun.plugin.dom.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;
import sun.plugin.dom.core.Document;
import sun.plugin.dom.css.ViewCSS;
import sun.plugin.dom.exception.PluginNotSupportedException;

public class HTMLDocument extends Document
  implements org.w3c.dom.html.HTMLDocument, DocumentView, DocumentStyle, DocumentCSS
{
  private static final String TAG_HTML = "HTML";

  public HTMLDocument(DOMObject paramDOMObject, org.w3c.dom.html.HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getTitle()
  {
    return getAttribute("title");
  }

  public void setTitle(String paramString)
  {
    setAttribute("title", paramString);
  }

  public String getReferrer()
  {
    return getAttribute("referrer");
  }

  public String getDomain()
  {
    return getAttribute("domain");
  }

  public String getURL()
  {
    return getAttribute("URL");
  }

  public org.w3c.dom.html.HTMLElement getBody()
  {
    Object localObject = this.obj.getMember("body");
    if (localObject == null)
      return null;
    return DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
  }

  public void setBody(org.w3c.dom.html.HTMLElement paramHTMLElement)
  {
    DOMObject localDOMObject = ((HTMLElement)paramHTMLElement).getDOMObject();
    this.obj.setMember("body", localDOMObject);
  }

  public HTMLCollection getImages()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("images"), this);
  }

  public HTMLCollection getApplets()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("applets"), this);
  }

  public HTMLCollection getLinks()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("links"), this);
  }

  public HTMLCollection getForms()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("forms"), this);
  }

  public HTMLCollection getAnchors()
  {
    return DOMObjectFactory.createHTMLCollection(this.obj.getMember("anchors"), this);
  }

  public String getCookie()
  {
    return getAttribute("cookie");
  }

  public void setCookie(String paramString)
  {
    setAttribute("cookie", paramString);
  }

  public void open()
  {
    throw new PluginNotSupportedException("HTMLDocument.open() is not supported");
  }

  public void close()
  {
    throw new PluginNotSupportedException("HTMLDocument.close() is not supported");
  }

  public void write(String paramString)
  {
    throw new PluginNotSupportedException("HTMLDocument.write() is not supported");
  }

  public void writeln(String paramString)
  {
    throw new PluginNotSupportedException("HTMLDocument.writeln() is not supported");
  }

  public NodeList getElementsByName(String paramString)
  {
    return DOMObjectFactory.createNodeList(this.obj.call("getElementsByName", new Object[] { paramString }), this);
  }

  public NodeList getElementsByTagName(String paramString)
  {
    return DOMObjectFactory.createNodeList(this.obj.call("getElementsByTagName", new Object[] { paramString }), this);
  }

  public Element getDocumentElement()
  {
    Object localObject = this.obj.getMember("documentElement");
    if (localObject == null)
      return null;
    return DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
  }

  public Element createElement(String paramString)
    throws DOMException
  {
    Object localObject = this.obj.call("createElement", new Object[] { paramString });
    if (localObject == null)
      return null;
    return DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
  }

  public Element createElementNS(String paramString1, String paramString2)
    throws DOMException
  {
    Object localObject = this.obj.call("createElementNS", new Object[] { paramString1, paramString2 });
    if (localObject == null)
      return null;
    return DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
  }

  public Element getElementById(String paramString)
  {
    Object localObject = this.obj.call("getElementById", new Object[] { paramString });
    if (localObject == null)
      return null;
    return DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
  }

  private String getAttribute(String paramString)
  {
    return DOMObjectHelper.getStringMember(this.obj, paramString);
  }

  private void setAttribute(String paramString1, String paramString2)
  {
    DOMObjectHelper.setStringMember(this.obj, paramString1, paramString2);
  }

  public AbstractView getDefaultView()
  {
    return new ViewCSS(this);
  }

  public StyleSheetList getStyleSheets()
  {
    return DOMObjectFactory.createStyleSheetList(this.obj.getMember("styleSheets"), this);
  }

  public CSSStyleDeclaration getOverrideStyle(Element paramElement, String paramString)
  {
    if ((paramElement instanceof ElementCSSInlineStyle))
    {
      ElementCSSInlineStyle localElementCSSInlineStyle = (ElementCSSInlineStyle)paramElement;
      return localElementCSSInlineStyle.getStyle();
    }
    return null;
  }

  public NodeList getChildNodes()
  {
    return getElementsByTagName("HTML");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLDocument
 * JD-Core Version:    0.6.2
 */