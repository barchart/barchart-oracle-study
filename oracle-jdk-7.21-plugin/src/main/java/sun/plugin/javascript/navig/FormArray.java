package sun.plugin.javascript.navig;

import netscape.javascript.JSException;

class FormArray extends Array
{
  FormArray(int paramInt1, String paramString, int paramInt2)
  {
    super(paramInt1, paramString, paramInt2);
  }

  protected Object createObject(String paramString)
    throws JSException
  {
    return resolveObject("[object Form]", paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.FormArray
 * JD-Core Version:    0.6.2
 */