package sun.plugin.dom.stylesheets;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.MediaList;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class StyleSheet
  implements org.w3c.dom.stylesheets.StyleSheet
{
  protected DOMObject obj;
  protected Document doc;

  public StyleSheet(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.doc = paramDocument;
  }

  public String getType()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "type");
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMemberNoEx(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMemberNoEx(this.obj, "disabled", paramBoolean);
  }

  public Node getOwnerNode()
  {
    return DOMObjectFactory.createNode(this.obj.getMember("ownerNode"), this.doc);
  }

  public org.w3c.dom.stylesheets.StyleSheet getParentStyleSheet()
  {
    return DOMObjectFactory.createStyleSheet(this.obj.getMember("parentStyleSheet"), this.doc);
  }

  public String getHref()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "href");
  }

  public String getTitle()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "title");
  }

  public MediaList getMedia()
  {
    try
    {
      return DOMObjectFactory.createMediaList(this.obj.getMember("media"), this.doc);
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.stylesheets.StyleSheet
 * JD-Core Version:    0.6.2
 */