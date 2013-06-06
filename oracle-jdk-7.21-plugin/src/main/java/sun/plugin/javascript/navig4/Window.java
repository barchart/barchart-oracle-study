package sun.plugin.javascript.navig4;

import java.util.HashMap;
import netscape.javascript.JSException;

public class Window extends sun.plugin.javascript.navig.Window
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();
  private static boolean factorySet = false;

  public Window(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    try
    {
      if (!factorySet)
      {
        setResolver(new JSObjectFactory());
        factorySet = true;
      }
    }
    catch (JSException localJSException)
    {
    }
    addObjectTable(fieldTable, methodTable);
  }

  public Window(int paramInt)
  {
    this(paramInt, "self");
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (paramString.equals("locationbar"))
      return resolveObject("[object UIBar]", this.context + ".locationbar");
    if (paramString.equals("menubar"))
      return resolveObject("[object UIBar]", this.context + ".menubar");
    if (paramString.equals("personalbar"))
      return resolveObject("[object UIBar]", this.context + ".personalbar");
    if (paramString.equals("scrollbars"))
      return resolveObject("[object UIBar]", this.context + ".scrollbars");
    if (paramString.equals("statusbar"))
      return resolveObject("[object UIBar]", this.context + ".statusbar");
    if (paramString.equals("toolbar"))
      return resolveObject("[object UIBar]", this.context + ".toolbar");
    return super.getMember(paramString);
  }

  static
  {
    methodTable.put("back", Boolean.FALSE);
    methodTable.put("clearInterval", Boolean.FALSE);
    methodTable.put("disableExternalCapture", Boolean.FALSE);
    methodTable.put("enableExternalCapture", Boolean.FALSE);
    methodTable.put("find", Boolean.TRUE);
    methodTable.put("forward", Boolean.FALSE);
    methodTable.put("home", Boolean.FALSE);
    methodTable.put("moveBy", Boolean.FALSE);
    methodTable.put("moveTo", Boolean.FALSE);
    methodTable.put("print", Boolean.FALSE);
    methodTable.put("resizeBy", Boolean.FALSE);
    methodTable.put("resizeTo", Boolean.FALSE);
    methodTable.put("scrollBy", Boolean.FALSE);
    methodTable.put("scrollTo", Boolean.FALSE);
    methodTable.put("setInterval", Boolean.FALSE);
    methodTable.put("stop", Boolean.FALSE);
    fieldTable.put("innerHeight", Boolean.TRUE);
    fieldTable.put("innerWidth", Boolean.TRUE);
    fieldTable.put("height", Boolean.TRUE);
    fieldTable.put("width", Boolean.TRUE);
    fieldTable.put("locationbar", Boolean.FALSE);
    fieldTable.put("menubar", Boolean.FALSE);
    fieldTable.put("outerHeight", Boolean.TRUE);
    fieldTable.put("outerWidth", Boolean.TRUE);
    fieldTable.put("pageXOffset", Boolean.TRUE);
    fieldTable.put("pageYOffset", Boolean.TRUE);
    fieldTable.put("personalbar", Boolean.FALSE);
    fieldTable.put("scrollbars", Boolean.FALSE);
    fieldTable.put("statusbar", Boolean.FALSE);
    fieldTable.put("toolbar", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.Window
 * JD-Core Version:    0.6.2
 */