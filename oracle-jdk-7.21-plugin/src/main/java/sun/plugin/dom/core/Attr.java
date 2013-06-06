package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;
import sun.plugin.dom.exception.PluginNotSupportedException;

public final class Attr extends Node
  implements org.w3c.dom.Attr
{
  private static final String ATTR_NAME = "name";
  private static final String ATTR_SPECIFIED = "specified";
  private static final String ATTR_VALUE = "value";

  public Attr(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getName()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "name");
  }

  public boolean getSpecified()
  {
    return DOMObjectHelper.getBooleanMemberNoEx(this.obj, "specified");
  }

  public String getValue()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "value");
  }

  public void setValue(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, "value", paramString);
  }

  public Element getOwnerElement()
  {
    return null;
  }

  public String getNodeValue()
    throws DOMException
  {
    return getValue();
  }

  public void setNodeValue(String paramString)
    throws DOMException
  {
    setValue(paramString);
  }

  public boolean isId()
  {
    throw new PluginNotSupportedException("Attr.isId() is not supported");
  }

  public TypeInfo getSchemaTypeInfo()
  {
    throw new PluginNotSupportedException("Attr.getSchemaTypeInfo() is not supported");
  }

  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
  {
    throw new PluginNotSupportedException("Attr.setUserData() is not supported");
  }

  public Object getUserData(String paramString)
  {
    throw new PluginNotSupportedException("Attr.getUserData() is not supported");
  }

  public boolean isEqualNode(org.w3c.dom.Node paramNode)
  {
    throw new PluginNotSupportedException("Attr.isEqualNode() is not supported");
  }

  public boolean isSameNode(org.w3c.dom.Node paramNode)
  {
    throw new PluginNotSupportedException("Attr.isSameNode() is not supported");
  }

  public String lookupNamespaceURI(String paramString)
  {
    throw new PluginNotSupportedException("Attr.lookupNamespaceURI() is not supported");
  }

  public boolean isDefaultNamespace(String paramString)
  {
    throw new PluginNotSupportedException("Attr.isDefaultNamespace() is not supported");
  }

  public String lookupPrefix(String paramString)
  {
    throw new PluginNotSupportedException("Attr.lookupPrefix() is not supported");
  }

  public short compareDocumentPosition(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    throw new PluginNotSupportedException("Attr.compareDocumentPosition() is not supported");
  }

  public String getBaseURI()
  {
    throw new PluginNotSupportedException("Attr.getBaseURI() is not supported");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Attr
 * JD-Core Version:    0.6.2
 */