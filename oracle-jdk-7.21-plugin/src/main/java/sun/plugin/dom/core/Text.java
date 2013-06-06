package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.exception.PluginNotSupportedException;

public class Text extends CharacterData
  implements org.w3c.dom.Text
{
  public Text(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public org.w3c.dom.Text splitText(int paramInt)
    throws DOMException
  {
    Object[] arrayOfObject = { new Integer(paramInt) };
    Object localObject = this.obj.call("splitText", arrayOfObject);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new Text((DOMObject)localObject, getOwnerDocument());
    return null;
  }

  public boolean isElementContentWhitespace()
  {
    throw new PluginNotSupportedException("Node.isElementContentWhitespace() is not supported.");
  }

  public String getWholeText()
  {
    throw new PluginNotSupportedException("Node.getWholeText() is not supported.");
  }

  public org.w3c.dom.Text replaceWholeText(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.replaceWholeText() is not supported.");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Text
 * JD-Core Version:    0.6.2
 */