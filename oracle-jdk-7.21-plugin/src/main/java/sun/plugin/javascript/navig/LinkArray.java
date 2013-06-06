package sun.plugin.javascript.navig;

import netscape.javascript.JSException;

public class LinkArray extends Array
{
  protected LinkArray(int paramInt1, String paramString, int paramInt2)
  {
    super(paramInt1, paramString, paramInt2);
  }

  protected Object createObject(String paramString)
    throws JSException
  {
    return resolveObject("[object Link]", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.LinkArray
 * JD-Core Version:    0.6.2
 */