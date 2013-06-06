package com.sun.deploy.xml;

public class XMLNodeBuilder
{
  private XMLNode _root;
  private XMLNode _last;

  public XMLNodeBuilder(String paramString, XMLAttribute paramXMLAttribute)
  {
    this._root = new XMLNode(paramString, paramXMLAttribute, null, null);
    this._last = null;
  }

  public void add(XMLNode paramXMLNode)
  {
    if (paramXMLNode == null)
      return;
    if (this._last == null)
    {
      this._root.setNested(paramXMLNode);
      this._last = paramXMLNode;
    }
    else
    {
      this._last.setNext(paramXMLNode);
      paramXMLNode.setNext(null);
      this._last = paramXMLNode;
    }
  }

  public void add(XMLable paramXMLable)
  {
    if (paramXMLable == null)
      return;
    add(paramXMLable.asXML());
  }

  public void add(String paramString1, String paramString2)
  {
    if (paramString2 != null)
      add(new XMLNode(paramString1, null, new XMLNode(paramString2), null));
  }

  public XMLNode getNode()
  {
    return this._root;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.xml.XMLNodeBuilder
 * JD-Core Version:    0.6.2
 */