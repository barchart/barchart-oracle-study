package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

class CharacterData extends Node
  implements org.w3c.dom.CharacterData
{
  private static final String ATTR_DATA = "data";
  private static final String ATTR_LENGTH = "length";
  private static final String FUNC_SUBSTRING_DATA = "substringData";
  private static final String FUNC_APPEND_DATA = "appendData";
  private static final String FUNC_INSERT_DATA = "insertData";
  private static final String FUNC_DELETE_DATA = "deleteData";
  private static final String FUNC_REPLACE_DATA = "replaceData";

  protected CharacterData(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getData()
    throws DOMException
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "data");
  }

  public void setData(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, "data", paramString);
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMemberNoEx(this.obj, "length");
  }

  public String substringData(int paramInt1, int paramInt2)
    throws DOMException
  {
    Object[] arrayOfObject = { new Integer(paramInt1), new Integer(paramInt2) };
    return DOMObjectHelper.callStringMethod(this.obj, "substringData", arrayOfObject);
  }

  public void appendData(String paramString)
    throws DOMException
  {
    Object[] arrayOfObject = { paramString };
    this.obj.call("appendData", arrayOfObject);
  }

  public void insertData(int paramInt, String paramString)
    throws DOMException
  {
    Object[] arrayOfObject = { new Integer(paramInt), paramString };
    this.obj.call("insertData", arrayOfObject);
  }

  public void deleteData(int paramInt1, int paramInt2)
    throws DOMException
  {
    Object[] arrayOfObject = { new Integer(paramInt1), new Integer(paramInt2) };
    this.obj.call("deleteData", arrayOfObject);
  }

  public void replaceData(int paramInt1, int paramInt2, String paramString)
    throws DOMException
  {
    Object[] arrayOfObject = { new Integer(paramInt1), new Integer(paramInt2), paramString };
    this.obj.call("replaceData", arrayOfObject);
  }

  public String getNodeValue()
    throws DOMException
  {
    return getData();
  }

  public void setNodeValue(String paramString)
    throws DOMException
  {
    setData(paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.CharacterData
 * JD-Core Version:    0.6.2
 */