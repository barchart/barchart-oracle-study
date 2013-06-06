package sun.plugin.dom.core;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.exception.PluginNotSupportedException;

public abstract class Document extends Node
  implements org.w3c.dom.Document
{
  private static final String FUNC_CREATE_DOC_FRAGMENT = "createDocumentFragment";
  private static final String FUNC_CREATE_TEXT_NODE = "createTextNode";
  private static final String FUNC_CREATE_COMMENT = "createComment";

  protected Document(DOMObject paramDOMObject, org.w3c.dom.Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public DocumentType getDoctype()
  {
    throw new PluginNotSupportedException("Document.getDoctype() is not supported.");
  }

  public org.w3c.dom.DOMImplementation getImplementation()
  {
    return new sun.plugin.dom.DOMImplementation((DOMObject)this.obj.getMember("implementation"));
  }

  public abstract Element getDocumentElement();

  public abstract Element createElement(String paramString)
    throws DOMException;

  public org.w3c.dom.DocumentFragment createDocumentFragment()
  {
    Object localObject = this.obj.call("createDocumentFragment", null);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new DocumentFragment((DOMObject)localObject, null);
    return null;
  }

  public org.w3c.dom.Text createTextNode(String paramString)
  {
    Object[] arrayOfObject = { paramString };
    Object localObject = this.obj.call("createTextNode", arrayOfObject);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new Text((DOMObject)localObject, null);
    return null;
  }

  public org.w3c.dom.Comment createComment(String paramString)
  {
    Object[] arrayOfObject = { paramString };
    Object localObject = this.obj.call("createComment", arrayOfObject);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new Comment((DOMObject)localObject, null);
    return null;
  }

  public CDATASection createCDATASection(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.createCDATASection() is not supported");
  }

  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.createProcessingInstruction() is not supported");
  }

  public Attr createAttribute(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.createAttribute() is not supported");
  }

  public EntityReference createEntityReference(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.createEntityReference() is not supported");
  }

  public abstract NodeList getElementsByTagName(String paramString);

  public org.w3c.dom.Node importNode(org.w3c.dom.Node paramNode, boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.importNode() is not supported");
  }

  public abstract Element createElementNS(String paramString1, String paramString2)
    throws DOMException;

  public Attr createAttributeNS(String paramString1, String paramString2)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.createAttributeNS() is not supported");
  }

  public NodeList getElementsByTagNameNS(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("Document.getElementsByTagNameNS() is not supported");
  }

  public abstract Element getElementById(String paramString);

  public String getNodeValue()
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.getNodeValue() is not supported");
  }

  public void setNodeValue(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.setNodeValue() is not supported");
  }

  public String getInputEncoding()
  {
    throw new PluginNotSupportedException("Document.getInputEncoding() is not supported");
  }

  public String getXmlEncoding()
  {
    throw new PluginNotSupportedException("Document.getXmlEncoding() is not supported");
  }

  public void setXmlEncoding(String paramString)
  {
    throw new PluginNotSupportedException("Document.setXmlEncoding() is not supported");
  }

  public boolean getXmlStandalone()
  {
    throw new PluginNotSupportedException("Document.getXmlStandalone() is not supported");
  }

  public void setXmlStandalone(boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.setXmlStandalone() is not supported");
  }

  public String getXmlVersion()
  {
    throw new PluginNotSupportedException("Document.getXmlVersion() is not supported");
  }

  public void setXmlVersion(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.setXmlVersion() is not supported");
  }

  public boolean getStrictErrorChecking()
  {
    throw new PluginNotSupportedException("Document.getStrictErrorChecking() is not supported");
  }

  public void setStrictErrorChecking(boolean paramBoolean)
  {
    throw new PluginNotSupportedException("Document.setStrictErrorChecking() is not supported");
  }

  public String getDocumentURI()
  {
    throw new PluginNotSupportedException("Document.getDocumentURI() is not supported");
  }

  public void setDocumentURI(String paramString)
  {
    throw new PluginNotSupportedException("Document.setDocumentURI() is not supported");
  }

  public org.w3c.dom.Node adoptNode(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.adoptNode() is not supported");
  }

  public DOMConfiguration getDomConfig()
  {
    throw new PluginNotSupportedException("Document.getDomConfig() is not supported");
  }

  public void normalizeDocument()
  {
    throw new PluginNotSupportedException("Document.normalizeDocument() is not supported");
  }

  public org.w3c.dom.Node renameNode(org.w3c.dom.Node paramNode, String paramString1, String paramString2)
    throws DOMException
  {
    throw new PluginNotSupportedException("Document.renameNode() is not supported");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Document
 * JD-Core Version:    0.6.2
 */