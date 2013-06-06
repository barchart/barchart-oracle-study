package sun.plugin.dom.html.common;

import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.plugin.dom.exception.PluginNotSupportedException;

public final class HTMLCollection
  implements NodeList, org.w3c.dom.html.HTMLCollection
{
  private ArrayList list = new ArrayList();

  public int getLength()
  {
    return this.list.size();
  }

  public Node item(int paramInt)
  {
    return (Node)this.list.get(paramInt);
  }

  public Node namedItem(String paramString)
  {
    throw new PluginNotSupportedException("HTMLCollection.namedItem() is not supported.");
  }

  public void add(Node paramNode)
  {
    this.list.add(paramNode);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.common.HTMLCollection
 * JD-Core Version:    0.6.2
 */