package sun.plugin.dom.html.ns4;

import netscape.javascript.JSObject;
import sun.plugin.dom.DOMObject;

public final class NS4DOMObject extends DOMObject
{
  public static final short TYPE_LINK = 1;
  public static final short TYPE_INPUT = 2;
  public static final short TYPE_IMAGE = 3;
  public static final short TYPE_APPLET = 4;
  public static final short TYPE_ANCHOR = 5;
  private short type;

  public NS4DOMObject(DOMObject paramDOMObject, short paramShort)
  {
    super((JSObject)paramDOMObject.getJSObject());
    this.type = paramShort;
  }

  public String toString()
  {
    switch (this.type)
    {
    case 1:
      return "[object HTMLLinkElement]";
    case 2:
      return "[object HTMLInputElement]";
    case 3:
      return "[object HTMLImageElement]";
    case 4:
      return "[object HTMLAppletElement]";
    case 5:
      return "[object HTMLAnchorElement]";
    }
    return super.toString();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.ns4.NS4DOMObject
 * JD-Core Version:    0.6.2
 */