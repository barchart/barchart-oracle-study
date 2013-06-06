package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.exception.PluginNotSupportedException;

public abstract class Node
  implements org.w3c.dom.Node
{
  protected DOMObject obj;
  private Document doc;

  protected Node(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.doc = paramDocument;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Node))
      return false;
    return this.obj.equals(((Node)paramObject).obj);
  }

  public int hashCode()
  {
    StringBuffer localStringBuffer = new StringBuffer(getClass().getName());
    localStringBuffer.append(toString());
    return localStringBuffer.toString().hashCode();
  }

  public String toString()
  {
    if (this.obj != null)
      return this.obj.toString();
    return super.toString();
  }

  public DOMObject getDOMObject()
  {
    return this.obj;
  }

  public String getNodeName()
  {
    return (String)this.obj.getMember("nodeName");
  }

  public abstract String getNodeValue()
    throws DOMException;

  public abstract void setNodeValue(String paramString)
    throws DOMException;

  public short getNodeType()
  {
    return ((Number)this.obj.getMember("nodeType")).shortValue();
  }

  public NodeList getChildNodes()
  {
    return DOMObjectFactory.createNodeList(this.obj.getMember("childNodes"), (HTMLDocument)getOwnerDocument());
  }

  public org.w3c.dom.Node getParentNode()
  {
    return DOMObjectFactory.createNode(this.obj.getMember("parentNode"), getOwnerDocument());
  }

  public org.w3c.dom.Node getFirstChild()
  {
    NodeList localNodeList = getChildNodes();
    if ((localNodeList != null) && (localNodeList.getLength() > 0))
      return localNodeList.item(0);
    return null;
  }

  public org.w3c.dom.Node getLastChild()
  {
    NodeList localNodeList = getChildNodes();
    int i;
    if ((localNodeList != null) && ((i = localNodeList.getLength()) > 0))
      return localNodeList.item(i - 1);
    return null;
  }

  public org.w3c.dom.Node getPreviousSibling()
  {
    return DOMObjectFactory.createNode(this.obj.getMember("previousSibling"), getOwnerDocument());
  }

  public org.w3c.dom.Node getNextSibling()
  {
    return DOMObjectFactory.createNode(this.obj.getMember("nextSibling"), (HTMLDocument)getOwnerDocument());
  }

  public NamedNodeMap getAttributes()
  {
    return DOMObjectFactory.createNamedNodeMap(this.obj.getMember("attributes"), (HTMLDocument)getOwnerDocument());
  }

  public Document getOwnerDocument()
  {
    return this.doc;
  }

  public org.w3c.dom.Node insertBefore(org.w3c.dom.Node paramNode1, org.w3c.dom.Node paramNode2)
    throws DOMException
  {
    if ((paramNode1 != null) && (paramNode2 != null))
    {
      if (((paramNode1 instanceof Node)) && ((paramNode2 instanceof Node)))
      {
        Node localNode1 = (Node)paramNode1;
        Node localNode2 = (Node)paramNode2;
        Object localObject = this.obj.call("insertBefore", new Object[] { localNode1.obj.getJSObject(), localNode2.obj.getJSObject() });
        return DOMObjectFactory.createNode(localObject, (HTMLDocument)getOwnerDocument());
      }
      throw new PluginNotSupportedException("Node.insertBefore() does not support node type: (" + paramNode1.getClass().getName() + ", " + paramNode2.getClass().getName() + ")");
    }
    return null;
  }

  public org.w3c.dom.Node replaceChild(org.w3c.dom.Node paramNode1, org.w3c.dom.Node paramNode2)
    throws DOMException
  {
    if ((paramNode1 != null) && (paramNode2 != null))
    {
      if (((paramNode1 instanceof Node)) && ((paramNode2 instanceof Node)))
      {
        Node localNode1 = (Node)paramNode1;
        Node localNode2 = (Node)paramNode2;
        Object localObject = this.obj.call("replaceChild", new Object[] { localNode1.obj.getJSObject(), localNode2.obj.getJSObject() });
        return DOMObjectFactory.createNode(localObject, (HTMLDocument)getOwnerDocument());
      }
      throw new PluginNotSupportedException("Node.replaceChild() does not support node type: (" + paramNode1.getClass().getName() + ", " + paramNode2.getClass().getName() + ")");
    }
    return null;
  }

  public org.w3c.dom.Node removeChild(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    if (paramNode != null)
    {
      if ((paramNode instanceof Node))
      {
        Node localNode = (Node)paramNode;
        Object localObject = this.obj.call("removeChild", new Object[] { localNode.obj.getJSObject() });
        return DOMObjectFactory.createNode(localObject, (HTMLDocument)getOwnerDocument());
      }
      throw new PluginNotSupportedException("Node.removeChild() does not support node type: " + paramNode.getClass().getName());
    }
    return null;
  }

  public org.w3c.dom.Node appendChild(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    if (paramNode != null)
    {
      if ((paramNode instanceof Node))
      {
        Node localNode = (Node)paramNode;
        Object localObject = this.obj.call("appendChild", new Object[] { localNode.obj.getJSObject() });
        return DOMObjectFactory.createNode(localObject, (HTMLDocument)getOwnerDocument());
      }
      throw new PluginNotSupportedException("Node.appendChild() does not support node type: " + paramNode.getClass().getName());
    }
    return null;
  }

  public boolean hasChildNodes()
  {
    return ((Boolean)this.obj.call("hasChildNodes", null)).booleanValue();
  }

  public org.w3c.dom.Node cloneNode(boolean paramBoolean)
  {
    Object localObject = this.obj.call("cloneNode", new Object[] { new Boolean(paramBoolean) });
    return DOMObjectFactory.createNode(localObject, (HTMLDocument)getOwnerDocument());
  }

  public void normalize()
  {
    this.obj.call("normalize", null);
  }

  public boolean isSupported(String paramString1, String paramString2)
  {
    return getOwnerDocument().getImplementation().hasFeature(paramString1, paramString2);
  }

  public String getNamespaceURI()
  {
    return (String)this.obj.getMember("namespaceURI");
  }

  public String getPrefix()
  {
    return (String)this.obj.getMember("prefix");
  }

  public void setPrefix(String paramString)
    throws DOMException
  {
    this.obj.setMember("prefix", paramString);
  }

  public String getLocalName()
  {
    return (String)this.obj.getMember("localName");
  }

  public boolean hasAttributes()
  {
    return ((Boolean)this.obj.call("hasAttributes", null)).booleanValue();
  }

  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
  {
    throw new PluginNotSupportedException("Node.setUserData() is not supported.");
  }

  public Object getUserData(String paramString)
  {
    throw new PluginNotSupportedException("Node.getUserData() is not supported.");
  }

  public Object getFeature(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("Node.getFeature() is not supported.");
  }

  public boolean isSameNode(org.w3c.dom.Node paramNode)
  {
    throw new PluginNotSupportedException("Node.isSameNode() is not supported.");
  }

  public boolean isEqualNode(org.w3c.dom.Node paramNode)
  {
    throw new PluginNotSupportedException("Node.isEqualNode() is not supported.");
  }

  public String lookupNamespaceURI(String paramString)
  {
    throw new PluginNotSupportedException("Node.lookupNamespaceURI() is not supported.");
  }

  public boolean isDefaultNamespace(String paramString)
  {
    throw new PluginNotSupportedException("Node.isDefaultNamespace() is not supported.");
  }

  public String lookupPrefix(String paramString)
  {
    throw new PluginNotSupportedException("Node.lookupPrefix() is not supported.");
  }

  public String getTextContent()
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.getTextContent() is not supported.");
  }

  public void setTextContent(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.setTextContent() is not supported.");
  }

  public short compareDocumentPosition(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.compareDocumentPosition() is not supported.");
  }

  public String getBaseURI()
  {
    throw new PluginNotSupportedException("Node.getBaseURI() is not supported.");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Node
 * JD-Core Version:    0.6.2
 */