package sun.plugin.dom.html;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLCollection
  implements org.w3c.dom.html.HTMLCollection, NodeList
{
  protected DOMObject obj;
  protected HTMLDocument doc;

  public HTMLCollection(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    this.obj = paramDOMObject;
    this.doc = paramHTMLDocument;
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMember(this.obj, "length");
  }

  public Node item(int paramInt)
  {
    return DOMObjectFactory.createNode(this.obj.getSlot(paramInt), this.doc);
  }

  public Node namedItem(String paramString)
  {
    return DOMObjectFactory.createNode(this.obj.call("namedItem", new Object[] { paramString }), this.doc);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLCollection
 * JD-Core Version:    0.6.2
 */