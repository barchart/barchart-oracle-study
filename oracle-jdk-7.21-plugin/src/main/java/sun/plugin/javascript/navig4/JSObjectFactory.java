package sun.plugin.javascript.navig4;

import netscape.javascript.JSException;
import sun.plugin.javascript.navig.JSObject;

public class JSObjectFactory extends sun.plugin.javascript.navig.JSObjectFactory
{
  public Object resolveObject(JSObject paramJSObject, String paramString1, int paramInt, String paramString2, Object paramObject)
    throws JSException
  {
    String str = paramString1;
    if ((paramString1 != null) && (paramString1.indexOf("[") != -1))
    {
      if (paramString1.indexOf("[object Window]") != -1)
        return new Window(paramInt, paramString2);
      if (paramString1.indexOf("[object Anchor]") != -1)
        return new Anchor(paramInt, paramString2);
      if (paramString1.indexOf("[object Document]") != -1)
        return new Document(paramInt, paramString2);
      if (paramString1.indexOf("[object Link]") != -1)
        return new Link(paramInt, paramString2);
      if (paramString1.indexOf("[object Layer]") != -1)
        return new Layer(paramInt, paramString2);
      if (paramString1.indexOf("[object Navigator]") != -1)
        return new Navigator(paramInt);
      if (paramString1.indexOf("[object UIBar]") != -1)
        return new UIBar(paramInt, paramString2);
      if (paramString1.indexOf("[object LayerArray]") != -1)
      {
        int i = 0;
        try
        {
          Object localObject = paramJSObject.eval(paramString2 + ".length");
          i = Integer.parseInt(localObject.toString().trim());
        }
        catch (Throwable localThrowable)
        {
          throw new JSException("resolveObject does not support " + toString() + ".length");
        }
        return new LayerArray(paramInt, paramString2, i);
      }
      return super.resolveObject(paramJSObject, paramString1, paramInt, paramString2, paramObject);
    }
    return str;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.JSObjectFactory
 * JD-Core Version:    0.6.2
 */