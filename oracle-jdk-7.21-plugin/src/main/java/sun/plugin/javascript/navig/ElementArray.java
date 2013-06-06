package sun.plugin.javascript.navig;

import netscape.javascript.JSException;

class ElementArray extends Array
{
  private Form form;

  ElementArray(int paramInt1, String paramString, int paramInt2, Form paramForm)
  {
    super(paramInt1, paramString, paramInt2);
    this.form = paramForm;
  }

  protected Object createObject(String paramString)
    throws JSException
  {
    return resolveObject("[object Element]", paramString, this.form);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.ElementArray
 * JD-Core Version:    0.6.2
 */