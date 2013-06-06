package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;
import sun.plugin.dom.exception.PluginNotSupportedException;
import sun.plugin.dom.html.HTMLDocument;

public abstract class Element extends Node
  implements org.w3c.dom.Element
{
  private static final String ATTR_TAGNAME = "tagName";

  protected Element(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getTagName()
  {
    return getAttribute("tagName");
  }

  public String getAttribute(String paramString)
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, paramString);
  }

  public void setAttribute(String paramString1, String paramString2)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, paramString1, paramString2);
  }

  public void removeAttribute(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.removeAttribute() is not supported");
  }

  public org.w3c.dom.Attr getAttributeNode(String paramString)
  {
    return DOMObjectFactory.createAttr(this.obj.call("getAttributeNode", new Object[] { paramString }), getOwnerDocument());
  }

  public org.w3c.dom.Attr setAttributeNode(org.w3c.dom.Attr paramAttr)
    throws DOMException
  {
    return DOMObjectFactory.createAttr(this.obj.call("setAttributeNode", new Object[] { ((Attr)paramAttr).getDOMObject() }), getOwnerDocument());
  }

  public org.w3c.dom.Attr removeAttributeNode(org.w3c.dom.Attr paramAttr)
    throws DOMException
  {
    return DOMObjectFactory.createAttr(this.obj.call("removeAttributeNode", new Object[] { ((Attr)paramAttr).getDOMObject() }), getOwnerDocument());
  }

  public NodeList getElementsByTagName(String paramString)
  {
    return DOMObjectFactory.createNodeList(this.obj.call("getElementsByTagName", new Object[] { paramString }), (HTMLDocument)getOwnerDocument());
  }

  public String getAttributeNS(String paramString1, String paramString2)
  {
    try
    {
      return (String)this.obj.call("getAttributeNS", new Object[] { paramString1, paramString2 });
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  public void setAttributeNS(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    this.obj.call("setAttributeNS", new Object[] { paramString1, paramString2, paramString3 });
  }

  public void removeAttributeNS(String paramString1, String paramString2)
    throws DOMException
  {
    this.obj.call("removeAttributeNS", new Object[] { paramString1, paramString2 });
  }

  public org.w3c.dom.Attr getAttributeNodeNS(String paramString1, String paramString2)
  {
    return DOMObjectFactory.createAttr(this.obj.call("getAttributeNodeNS", new Object[] { paramString1, paramString2 }), getOwnerDocument());
  }

  public org.w3c.dom.Attr setAttributeNodeNS(org.w3c.dom.Attr paramAttr)
    throws DOMException
  {
    return DOMObjectFactory.createAttr(this.obj.call("setAttributeNodeNS", new Object[] { ((Attr)paramAttr).getDOMObject() }), getOwnerDocument());
  }

  public NodeList getElementsByTagNameNS(String paramString1, String paramString2)
  {
    return DOMObjectFactory.createNodeList(this.obj.call("getElementsByTagNameNS", new Object[] { paramString1, paramString2 }), (HTMLDocument)getOwnerDocument());
  }

  public boolean hasAttribute(String paramString)
  {
    return getAttribute(paramString) != null;
  }

  public boolean hasAttributeNS(String paramString1, String paramString2)
  {
    try
    {
      return ((Boolean)this.obj.call("hasAttributeNS", new Object[] { paramString1, paramString2 })).booleanValue();
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  public String getNodeValue()
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.getNodeValue() is not supported");
  }

  public void setNodeValue(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setNodeValue() is not supported");
  }

  public void setIdAttribute(String paramString, boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setIdAttribute() is not supported");
  }

  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setIdAttributeNS() is not supported");
  }

  public void setIdAttributeNode(org.w3c.dom.Attr paramAttr, boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setIdAttributeNode() is not supported");
  }

  public TypeInfo getSchemaTypeInfo()
  {
    throw new PluginNotSupportedException("Element.getSchemaTypeInfo is not supported");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Element
 * JD-Core Version:    0.6.2
 */