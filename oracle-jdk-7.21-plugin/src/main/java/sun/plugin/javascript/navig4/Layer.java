package sun.plugin.javascript.navig4;

import java.util.HashMap;
import netscape.javascript.JSException;
import sun.plugin.javascript.navig.JSObject;

public class Layer extends JSObject
{
  private static HashMap fieldTable = new HashMap();
  private static HashMap methodTable = new HashMap();

  protected Layer(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, methodTable);
  }

  public Object getMember(String paramString)
    throws JSException
  {
    Object localObject;
    if (paramString.equals("document"))
    {
      localObject = evaluate(this.context + ".document");
      if (localObject == null)
        return null;
      return resolveObject("[object Document]", this.context + ".document");
    }
    if (paramString.equals("siblingAbove"))
    {
      localObject = evaluate(this.context + ".siblingAbove");
      if (localObject == null)
        return null;
      return resolveObject("[object Layer]", this.context + ".siblingAbove");
    }
    if (paramString.equals("siblingBelow"))
    {
      localObject = evaluate(this.context + ".siblingBelow");
      if (localObject == null)
        return null;
      return resolveObject("[object Layer]", this.context + ".siblingBelow");
    }
    if (paramString.equals("above"))
    {
      localObject = evaluate(this.context + ".above");
      if (localObject == null)
        return null;
      return resolveObject("[object Layer]", this.context + ".above");
    }
    if (paramString.equals("below"))
    {
      localObject = evaluate(this.context + ".below");
      if (localObject == null)
        return null;
      return resolveObject("[object Layer]", this.context + ".below");
    }
    if (paramString.equals("parentLayer"))
    {
      localObject = evaluate(this.context + ".parentLayer");
      if (localObject == null)
        return null;
      return resolveObject("[object Layer]", this.context + ".parentLayer");
    }
    return super.getMember(paramString);
  }

  static
  {
    fieldTable.put("document", Boolean.FALSE);
    fieldTable.put("name", Boolean.FALSE);
    fieldTable.put("left", Boolean.TRUE);
    fieldTable.put("top", Boolean.TRUE);
    fieldTable.put("pageX", Boolean.TRUE);
    fieldTable.put("pageY", Boolean.TRUE);
    fieldTable.put("zIndex", Boolean.TRUE);
    fieldTable.put("visibility", Boolean.TRUE);
    fieldTable.put("clip.top", Boolean.TRUE);
    fieldTable.put("clip.left", Boolean.TRUE);
    fieldTable.put("clip.right", Boolean.TRUE);
    fieldTable.put("clip.bottom", Boolean.TRUE);
    fieldTable.put("clip.width", Boolean.TRUE);
    fieldTable.put("clip.height", Boolean.TRUE);
    fieldTable.put("clip.top", Boolean.TRUE);
    fieldTable.put("background", Boolean.TRUE);
    fieldTable.put("bgColor", Boolean.TRUE);
    fieldTable.put("siblingAbove", Boolean.FALSE);
    fieldTable.put("siblingBelow", Boolean.FALSE);
    fieldTable.put("above", Boolean.FALSE);
    fieldTable.put("below", Boolean.FALSE);
    fieldTable.put("parentLayer", Boolean.FALSE);
    fieldTable.put("src", Boolean.TRUE);
    methodTable.put("moveBy", Boolean.FALSE);
    methodTable.put("moveTo", Boolean.FALSE);
    methodTable.put("moveToAbsolute", Boolean.FALSE);
    methodTable.put("resizeBy", Boolean.FALSE);
    methodTable.put("resizeTo", Boolean.FALSE);
    methodTable.put("moveAbove", Boolean.FALSE);
    methodTable.put("moveBelow", Boolean.FALSE);
    methodTable.put("load", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.Layer
 * JD-Core Version:    0.6.2
 */