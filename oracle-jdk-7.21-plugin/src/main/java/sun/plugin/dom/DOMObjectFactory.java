package sun.plugin.dom;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.core.CDATASection;
import sun.plugin.dom.core.Comment;
import sun.plugin.dom.core.DocumentFragment;
import sun.plugin.dom.core.Text;
import sun.plugin.dom.css.CSSCharsetRule;
import sun.plugin.dom.css.CSSFontFaceRule;
import sun.plugin.dom.css.CSSImportRule;
import sun.plugin.dom.css.CSSMediaRule;
import sun.plugin.dom.css.CSSPageRule;
import sun.plugin.dom.css.CSSStyleRule;
import sun.plugin.dom.css.CSSUnknownRule;
import sun.plugin.dom.css.CSSValueList;
import sun.plugin.dom.exception.PluginNotSupportedException;
import sun.plugin.dom.html.HTMLAnchorElement;
import sun.plugin.dom.html.HTMLAppletElement;
import sun.plugin.dom.html.HTMLAreaElement;
import sun.plugin.dom.html.HTMLBRElement;
import sun.plugin.dom.html.HTMLBaseElement;
import sun.plugin.dom.html.HTMLBodyElement;
import sun.plugin.dom.html.HTMLDListElement;
import sun.plugin.dom.html.HTMLDirectoryElement;
import sun.plugin.dom.html.HTMLDivElement;
import sun.plugin.dom.html.HTMLFieldSetElement;
import sun.plugin.dom.html.HTMLFontElement;
import sun.plugin.dom.html.HTMLFrameElement;
import sun.plugin.dom.html.HTMLFrameSetElement;
import sun.plugin.dom.html.HTMLHRElement;
import sun.plugin.dom.html.HTMLHeadElement;
import sun.plugin.dom.html.HTMLHeadingElement;
import sun.plugin.dom.html.HTMLHtmlElement;
import sun.plugin.dom.html.HTMLIFrameElement;
import sun.plugin.dom.html.HTMLImageElement;
import sun.plugin.dom.html.HTMLInputElement;
import sun.plugin.dom.html.HTMLIsIndexElement;
import sun.plugin.dom.html.HTMLLIElement;
import sun.plugin.dom.html.HTMLLabelElement;
import sun.plugin.dom.html.HTMLLegendElement;
import sun.plugin.dom.html.HTMLLinkElement;
import sun.plugin.dom.html.HTMLMapElement;
import sun.plugin.dom.html.HTMLMenuElement;
import sun.plugin.dom.html.HTMLMetaElement;
import sun.plugin.dom.html.HTMLModElement;
import sun.plugin.dom.html.HTMLOListElement;
import sun.plugin.dom.html.HTMLObjectElement;
import sun.plugin.dom.html.HTMLOptGroupElement;
import sun.plugin.dom.html.HTMLParagraphElement;
import sun.plugin.dom.html.HTMLParamElement;
import sun.plugin.dom.html.HTMLPreElement;
import sun.plugin.dom.html.HTMLQuoteElement;
import sun.plugin.dom.html.HTMLScriptElement;
import sun.plugin.dom.html.HTMLSelectElement;
import sun.plugin.dom.html.HTMLStyleElement;
import sun.plugin.dom.html.HTMLTableCaptionElement;
import sun.plugin.dom.html.HTMLTableCellElement;
import sun.plugin.dom.html.HTMLTableColElement;
import sun.plugin.dom.html.HTMLTableElement;
import sun.plugin.dom.html.HTMLTableRowElement;
import sun.plugin.dom.html.HTMLTableSectionElement;
import sun.plugin.dom.html.HTMLTextAreaElement;
import sun.plugin.dom.html.HTMLTitleElement;
import sun.plugin.dom.html.HTMLUListElement;

public class DOMObjectFactory
{
  private static HashMap elmTagClassMap = null;

  public static Node createNode(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
    {
      DOMObject localDOMObject = (DOMObject)paramObject;
      int i = ((Number)localDOMObject.getMember("nodeType")).intValue();
      switch (i)
      {
      case 1:
        return createHTMLElement(localDOMObject, (HTMLDocument)paramDocument);
      case 2:
        return createAttr(localDOMObject, paramDocument);
      case 3:
        return new Text(localDOMObject, paramDocument);
      case 4:
        return new CDATASection(localDOMObject, paramDocument);
      case 5:
        throw new PluginNotSupportedException("Entity reference nodes are not supported");
      case 6:
        throw new PluginNotSupportedException("Entity nodes are not supported");
      case 7:
        throw new PluginNotSupportedException("Processing instruction nodes are not supported");
      case 8:
        return new Comment(localDOMObject, paramDocument);
      case 9:
        return createHTMLElement(localDOMObject, (HTMLDocument)paramDocument);
      case 10:
        throw new PluginNotSupportedException("Document type nodes are not supported");
      case 11:
        return new DocumentFragment(localDOMObject, paramDocument);
      case 12:
        throw new PluginNotSupportedException("Notation nodes are not supported");
      }
      throw new PluginNotSupportedException("Unknown node type " + i);
    }
    return null;
  }

  public static org.w3c.dom.html.HTMLElement createHTMLElement(Object paramObject, HTMLDocument paramHTMLDocument)
  {
    if ((paramObject == null) || (!(paramObject instanceof DOMObject)))
      return null;
    Class localClass = getRealClassByTagName((DOMObject)paramObject);
    if (localClass != null)
      try
      {
        Class[] arrayOfClass = { DOMObject.class, HTMLDocument.class };
        Constructor localConstructor = localClass.getConstructor(arrayOfClass);
        Object[] arrayOfObject = { paramObject, paramHTMLDocument };
        return (org.w3c.dom.html.HTMLElement)localConstructor.newInstance(arrayOfObject);
      }
      catch (Exception localException)
      {
      }
    return new sun.plugin.dom.html.HTMLElement((DOMObject)paramObject, paramHTMLDocument);
  }

  public static org.w3c.dom.html.HTMLFormElement createHTMLFormElement(Object paramObject, HTMLDocument paramHTMLDocument)
  {
    org.w3c.dom.html.HTMLElement localHTMLElement = createHTMLElement(paramObject, paramHTMLDocument);
    if ((localHTMLElement != null) && ((localHTMLElement instanceof org.w3c.dom.html.HTMLFormElement)))
      return (org.w3c.dom.html.HTMLFormElement)localHTMLElement;
    return null;
  }

  public static org.w3c.dom.html.HTMLOptionElement createHTMLOptionElement(Object paramObject, HTMLDocument paramHTMLDocument)
  {
    org.w3c.dom.html.HTMLElement localHTMLElement = createHTMLElement(paramObject, paramHTMLDocument);
    if ((localHTMLElement != null) && ((localHTMLElement instanceof org.w3c.dom.html.HTMLOptionElement)))
      return (org.w3c.dom.html.HTMLOptionElement)localHTMLElement;
    return null;
  }

  public static NodeList createNodeList(Object paramObject, HTMLDocument paramHTMLDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.html.HTMLCollection((DOMObject)paramObject, paramHTMLDocument);
    return null;
  }

  public static org.w3c.dom.NamedNodeMap createNamedNodeMap(Object paramObject, HTMLDocument paramHTMLDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.core.NamedNodeMap((DOMObject)paramObject, paramHTMLDocument);
    return null;
  }

  public static org.w3c.dom.html.HTMLCollection createHTMLCollection(Object paramObject, HTMLDocument paramHTMLDocument)
  {
    return (org.w3c.dom.html.HTMLCollection)createNodeList(paramObject, paramHTMLDocument);
  }

  public static org.w3c.dom.Attr createAttr(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.core.Attr((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.stylesheets.StyleSheetList createStyleSheetList(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.stylesheets.StyleSheetList((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.stylesheets.StyleSheet createStyleSheet(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.stylesheets.StyleSheet((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.stylesheets.MediaList createMediaList(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.stylesheets.MediaList((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.css.CSSStyleSheet createCSSStyleSheet(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.css.CSSStyleSheet((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.css.CSSStyleDeclaration createCSSStyleDeclaration(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.css.CSSStyleDeclaration((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.css.CSSValue createCSSValue(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      try
      {
        Number localNumber = (Number)((DOMObject)paramObject).getMember("cssValueType");
        switch (localNumber.intValue())
        {
        case 0:
        case 3:
          return new sun.plugin.dom.css.CSSValue((DOMObject)paramObject, paramDocument);
        case 1:
          return new sun.plugin.dom.css.CSSPrimitiveValue((DOMObject)paramObject, paramDocument);
        case 2:
          return new CSSValueList((DOMObject)paramObject, paramDocument);
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    return null;
  }

  public static org.w3c.dom.css.CSSPrimitiveValue createCSSPrimitiveValue(Object paramObject, Document paramDocument)
  {
    org.w3c.dom.css.CSSValue localCSSValue = createCSSValue(paramObject, paramDocument);
    if ((localCSSValue != null) && ((localCSSValue instanceof org.w3c.dom.css.CSSPrimitiveValue)))
      return (org.w3c.dom.css.CSSPrimitiveValue)localCSSValue;
    return null;
  }

  public static CSSRule createCSSRule(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      try
      {
        DOMObject localDOMObject = (DOMObject)paramObject;
        int i = ((Number)localDOMObject.getMember("type")).intValue();
        switch (i)
        {
        case 0:
          return new CSSUnknownRule(localDOMObject, paramDocument);
        case 1:
          return new CSSStyleRule(localDOMObject, paramDocument);
        case 2:
          return new CSSCharsetRule(localDOMObject, paramDocument);
        case 3:
          return new CSSImportRule(localDOMObject, paramDocument);
        case 4:
          return new CSSMediaRule(localDOMObject, paramDocument);
        case 5:
          return new CSSFontFaceRule(localDOMObject, paramDocument);
        case 6:
          return new CSSPageRule(localDOMObject, paramDocument);
        }
      }
      catch (Exception localException)
      {
      }
    return null;
  }

  public static org.w3c.dom.css.CSSRuleList createCSSRuleList(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.css.CSSRuleList((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.css.Counter createCSSCounter(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.css.Counter((DOMObject)paramObject);
    return null;
  }

  public static org.w3c.dom.css.Rect createCSSRect(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.css.Rect((DOMObject)paramObject, paramDocument);
    return null;
  }

  public static org.w3c.dom.css.RGBColor createCSSRGBColor(Object paramObject, Document paramDocument)
  {
    if ((paramObject != null) && ((paramObject instanceof DOMObject)))
      return new sun.plugin.dom.css.RGBColor((DOMObject)paramObject, paramDocument);
    return null;
  }

  private static Class getRealClassByTagName(DOMObject paramDOMObject)
  {
    try
    {
      Object localObject = paramDOMObject.getMember("tagName");
      if (localObject != null)
        return (Class)getElmTagClassMap().get(localObject);
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  private static synchronized HashMap getElmTagClassMap()
  {
    if (elmTagClassMap == null)
    {
      elmTagClassMap = new HashMap();
      elmTagClassMap.put("A", HTMLAnchorElement.class);
      elmTagClassMap.put("APPLET", HTMLAppletElement.class);
      elmTagClassMap.put("AREA", HTMLAreaElement.class);
      elmTagClassMap.put("BASE", HTMLBaseElement.class);
      elmTagClassMap.put("BLOCKQUOTE", HTMLQuoteElement.class);
      elmTagClassMap.put("BODY", HTMLBodyElement.class);
      elmTagClassMap.put("BR", HTMLBRElement.class);
      elmTagClassMap.put("CAPTION", HTMLTableCaptionElement.class);
      elmTagClassMap.put("COL", HTMLTableColElement.class);
      elmTagClassMap.put("DEL", HTMLModElement.class);
      elmTagClassMap.put("DIR", HTMLDirectoryElement.class);
      elmTagClassMap.put("DIV", HTMLDivElement.class);
      elmTagClassMap.put("DL", HTMLDListElement.class);
      elmTagClassMap.put("FIELDSET", HTMLFieldSetElement.class);
      elmTagClassMap.put("FONT", HTMLFontElement.class);
      elmTagClassMap.put("FORM", sun.plugin.dom.html.HTMLFormElement.class);
      elmTagClassMap.put("FRAME", HTMLFrameElement.class);
      elmTagClassMap.put("FRAMESET", HTMLFrameSetElement.class);
      elmTagClassMap.put("HEAD", HTMLHeadElement.class);
      elmTagClassMap.put("H1", HTMLHeadingElement.class);
      elmTagClassMap.put("H2", HTMLHeadingElement.class);
      elmTagClassMap.put("H3", HTMLHeadingElement.class);
      elmTagClassMap.put("H4", HTMLHeadingElement.class);
      elmTagClassMap.put("H5", HTMLHeadingElement.class);
      elmTagClassMap.put("H6", HTMLHeadingElement.class);
      elmTagClassMap.put("HR", HTMLHRElement.class);
      elmTagClassMap.put("HTML", HTMLHtmlElement.class);
      elmTagClassMap.put("IFRAME", HTMLIFrameElement.class);
      elmTagClassMap.put("IMAGE", HTMLImageElement.class);
      elmTagClassMap.put("INPUT", HTMLInputElement.class);
      elmTagClassMap.put("INS", HTMLModElement.class);
      elmTagClassMap.put("ISINDEX", HTMLIsIndexElement.class);
      elmTagClassMap.put("LABEL", HTMLLabelElement.class);
      elmTagClassMap.put("LEGEND", HTMLLegendElement.class);
      elmTagClassMap.put("LI", HTMLLIElement.class);
      elmTagClassMap.put("LINK", HTMLLinkElement.class);
      elmTagClassMap.put("MAP", HTMLMapElement.class);
      elmTagClassMap.put("MENU", HTMLMenuElement.class);
      elmTagClassMap.put("META", HTMLMetaElement.class);
      elmTagClassMap.put("MOD", HTMLModElement.class);
      elmTagClassMap.put("OBJECT", HTMLObjectElement.class);
      elmTagClassMap.put("OL", HTMLOListElement.class);
      elmTagClassMap.put("OPTGROUP", HTMLOptGroupElement.class);
      elmTagClassMap.put("OPTION", sun.plugin.dom.html.HTMLOptionElement.class);
      elmTagClassMap.put("P", HTMLParagraphElement.class);
      elmTagClassMap.put("PARAM", HTMLParamElement.class);
      elmTagClassMap.put("PRE", HTMLPreElement.class);
      elmTagClassMap.put("Q", HTMLQuoteElement.class);
      elmTagClassMap.put("SCRIPT", HTMLScriptElement.class);
      elmTagClassMap.put("SELECT", HTMLSelectElement.class);
      elmTagClassMap.put("STYLE", HTMLStyleElement.class);
      elmTagClassMap.put("TABLE", HTMLTableElement.class);
      elmTagClassMap.put("TBODY", HTMLTableSectionElement.class);
      elmTagClassMap.put("TD", HTMLTableCellElement.class);
      elmTagClassMap.put("TFOOT", HTMLTableSectionElement.class);
      elmTagClassMap.put("TH", HTMLTableCellElement.class);
      elmTagClassMap.put("THEAD", HTMLTableSectionElement.class);
      elmTagClassMap.put("TR", HTMLTableRowElement.class);
      elmTagClassMap.put("TEXTAREA", HTMLTextAreaElement.class);
      elmTagClassMap.put("TITLE", HTMLTitleElement.class);
      elmTagClassMap.put("UL", HTMLUListElement.class);
      elmTagClassMap.put("ACRONYM", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("ABBR", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("ADDRESS", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("B", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("BDO", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("BIG", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("CITE", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("CENTER", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("CODE", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("DD", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("DFN", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("DT", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("EM", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("I", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("KBD", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("NOFRAMES", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("NOSCRIPT", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("S", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("SAMP", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("SMALL", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("SPAN", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("STRIKE", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("STRONG", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("SUB", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("SUP", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("U", sun.plugin.dom.html.HTMLElement.class);
      elmTagClassMap.put("VAR", sun.plugin.dom.html.HTMLElement.class);
    }
    return elmTagClassMap;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMObjectFactory
 * JD-Core Version:    0.6.2
 */