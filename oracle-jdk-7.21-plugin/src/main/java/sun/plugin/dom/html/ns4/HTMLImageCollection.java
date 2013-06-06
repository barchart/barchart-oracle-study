package sun.plugin.dom.html.ns4;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.html.HTMLCollection;

public final class HTMLImageCollection extends HTMLCollection
{
  public HTMLImageCollection(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public Node item(int paramInt)
  {
    Object localObject = this.obj.getSlot(paramInt);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return DOMObjectFactory.createHTMLElement(new NS4DOMObject((DOMObject)localObject, (short)3), this.doc);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.ns4.HTMLImageCollection
 * JD-Core Version:    0.6.2
 */