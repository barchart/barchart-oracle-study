package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public class NamedNodeMap
  implements org.w3c.dom.NamedNodeMap
{
  private DOMObject obj;
  private HTMLDocument document;

  public NamedNodeMap(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    this.obj = paramDOMObject;
    this.document = paramHTMLDocument;
  }

  public Node getNamedItem(String paramString)
  {
    return DOMObjectFactory.createNode(this.obj.call("getNamedItem", new Object[] { paramString }), this.document);
  }

  public Node setNamedItem(Node paramNode)
    throws DOMException
  {
    return DOMObjectFactory.createNode(this.obj.call("setNamedItem", new Object[] { paramNode }), this.document);
  }

  public Node removeNamedItem(String paramString)
    throws DOMException
  {
    return DOMObjectFactory.createNode(this.obj.call("removeNamedItem", new Object[] { paramString }), this.document);
  }

  public Node item(int paramInt)
  {
    return DOMObjectFactory.createNode(this.obj.call("item", new Object[] { new Integer(paramInt) }), this.document);
  }

  public int getLength()
  {
    return ((Number)this.obj.getMember("length")).intValue();
  }

  public Node getNamedItemNS(String paramString1, String paramString2)
    throws DOMException
  {
    return DOMObjectFactory.createNode(this.obj.call("getNamedItemNS", new Object[] { paramString1, paramString2 }), this.document);
  }

  public Node setNamedItemNS(Node paramNode)
    throws DOMException
  {
    return DOMObjectFactory.createNode(this.obj.call("setNamedItemNS", new Object[] { paramNode }), this.document);
  }

  public Node removeNamedItemNS(String paramString1, String paramString2)
    throws DOMException
  {
    return DOMObjectFactory.createNode(this.obj.call("removeNamedItemNS", new Object[] { paramString1, paramString2 }), this.document);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.NamedNodeMap
 * JD-Core Version:    0.6.2
 */