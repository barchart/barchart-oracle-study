package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.exception.PluginNotSupportedException;

public final class DocumentFragment extends Node
  implements org.w3c.dom.DocumentFragment
{
  public DocumentFragment(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getNodeValue()
    throws DOMException
  {
    throw new PluginNotSupportedException("DocumentFragment.getNodeValue() is not supported");
  }

  public void setNodeValue(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("DocumentFragment.setNodeValue() is not supported");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.DocumentFragment
 * JD-Core Version:    0.6.2
 */