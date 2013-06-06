package sun.plugin.javascript.navig;

import netscape.javascript.JSException;

public class JSObjectFactory
  implements JSObjectResolver
{
  public Object resolveObject(JSObject paramJSObject, String paramString1, int paramInt, String paramString2, Object paramObject)
    throws JSException
  {
    String str = paramString1;
    if ((paramString1 != null) && (paramString1.indexOf("[") != -1))
    {
      if (paramString1.indexOf("Array]") == -1)
      {
        if (paramString1.indexOf("[object Window]") != -1)
          return new Window(paramInt, paramString2);
        if (paramString1.indexOf("[object Anchor]") != -1)
          return new Anchor(paramInt, paramString2);
        if (paramString1.indexOf("[object Document]") != -1)
          return new Document(paramInt, paramString2);
        if (paramString1.indexOf("[object Element]") != -1)
          return new Element(paramInt, paramString2, (Form)paramObject);
        if (paramString1.indexOf("[object Form]") != -1)
          return new Form(paramInt, paramString2);
        if (paramString1.indexOf("[object History]") != -1)
          return new History(paramInt, paramString2);
        if (paramString1.indexOf("[object Image]") != -1)
          return new Image(paramInt, paramString2);
        if (paramString1.indexOf("[object Link]") != -1)
          return new Link(paramInt, paramString2);
        if (paramString1.indexOf("[object Location]") != -1)
          return new Location(paramInt, paramString2);
        if (paramString1.indexOf("[object Navigator]") != -1)
          return new Navigator(paramInt);
        if (paramString1.indexOf("[object Option]") != -1)
          return new Option(paramInt, paramString2);
        if (paramString1.indexOf("[object URL]") != -1)
          return new URL(paramInt, paramString2);
        throw new JSException(paramString1 + " cannot be resolved as JSObject.");
      }
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
      if (paramString1.indexOf("[object AnchorArray]") != -1)
        return new AnchorArray(paramInt, paramString2, i);
      if (paramString1.indexOf("[object ElementArray]") != -1)
        return new ElementArray(paramInt, paramString2, i, (Form)paramObject);
      if (paramString1.indexOf("[object FormArray]") != -1)
        return new FormArray(paramInt, paramString2, i);
      if (paramString1.indexOf("[object FrameArray]") != -1)
        return new FrameArray(paramInt, paramString2, i);
      if (paramString1.indexOf("[object ImageArray]") != -1)
        return new ImageArray(paramInt, paramString2, i);
      if (paramString1.indexOf("[object LinkArray]") != -1)
        return new LinkArray(paramInt, paramString2, i);
      if (paramString1.indexOf("[object OptionArray]") != -1)
        return new OptionArray(paramInt, paramString2, i);
      if (paramString1.indexOf("[object AppletArray]") != -1)
        return new AnchorArray(paramInt, paramString2, i);
      if (paramString1.indexOf("[object EmbedArray]") != -1)
        return new AnchorArray(paramInt, paramString2, i);
      throw new JSException(paramString1 + " cannot be resolved as JSObject.");
    }
    return str;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.JSObjectFactory
 * JD-Core Version:    0.6.2
 */