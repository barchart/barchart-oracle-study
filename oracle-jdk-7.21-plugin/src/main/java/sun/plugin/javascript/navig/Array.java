package sun.plugin.javascript.navig;

import netscape.javascript.JSException;

public abstract class Array extends JSObject
{
  protected int length = -1;

  protected Array(int paramInt1, String paramString, int paramInt2)
  {
    super(paramInt1, paramString);
    this.length = paramInt2;
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (paramString.equals("length"))
      return new Integer(this.length);
    return super.getMember(paramString);
  }

  public Object getSlot(int paramInt)
    throws JSException
  {
    if ((paramInt < 0) || (paramInt >= this.length))
      throw new JSException("getSlot does not support " + toString() + "[" + paramInt + "]");
    return createObject(this.context + "[" + paramInt + "]");
  }

  public void setSlot(int paramInt, Object paramObject)
    throws JSException
  {
    if ((paramInt < 0) || (paramInt >= this.length))
      throw new JSException("setSlot does not support " + toString() + "[" + paramInt + "]");
    evaluate(this.context + "[" + paramInt + "]=" + paramObject.toString());
  }

  protected abstract Object createObject(String paramString)
    throws JSException;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Array
 * JD-Core Version:    0.6.2
 */