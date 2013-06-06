package sun.plugin.dom.stylesheets;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class MediaList
  implements org.w3c.dom.stylesheets.MediaList
{
  private DOMObject obj;
  private Document doc;

  public MediaList(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.doc = paramDocument;
  }

  public String getMediaText()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "mediaText");
  }

  public void setMediaText(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, "mediaText", paramString);
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMemberNoEx(this.obj, "length");
  }

  public String item(int paramInt)
  {
    try
    {
      Object localObject = this.obj.getSlot(paramInt);
      if (localObject != null)
        return localObject.toString();
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  public void deleteMedium(String paramString)
    throws DOMException
  {
    DOMObjectHelper.callStringMethod(this.obj, "deleteMedium", new Object[] { paramString });
  }

  public void appendMedium(String paramString)
    throws DOMException
  {
    DOMObjectHelper.callStringMethod(this.obj, "appendMedium", new Object[] { paramString });
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.stylesheets.MediaList
 * JD-Core Version:    0.6.2
 */