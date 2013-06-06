package sun.plugin.javascript.navig4;

import netscape.javascript.JSException;
import sun.plugin.javascript.navig.Array;

public class LayerArray extends Array
{
  protected LayerArray(int paramInt1, String paramString, int paramInt2)
  {
    super(paramInt1, paramString, paramInt2);
  }

  protected Object createObject(String paramString)
    throws JSException
  {
    return resolveObject("[object Layer]", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.LayerArray
 * JD-Core Version:    0.6.2
 */