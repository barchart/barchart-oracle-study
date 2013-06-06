package sun.plugin.javascript;

import netscape.javascript.JSException;

public abstract class JSObject extends netscape.javascript.JSObject
{
  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    throw new JSException("call does not support " + toString() + "." + paramString);
  }

  public Object eval(String paramString)
    throws JSException
  {
    throw new JSException("eval does not support " + paramString);
  }

  public Object getMember(String paramString)
    throws JSException
  {
    throw new JSException("getMember does not support " + toString() + "." + paramString);
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    throw new JSException("setMember does not support " + toString() + "." + paramString);
  }

  public void removeMember(String paramString)
    throws JSException
  {
    throw new JSException("removeMember does not support " + toString() + "." + paramString);
  }

  public Object getSlot(int paramInt)
    throws JSException
  {
    throw new JSException("getSlot does not support " + toString() + "[" + paramInt + "]");
  }

  public void setSlot(int paramInt, Object paramObject)
    throws JSException
  {
    throw new JSException("setSlot does not support " + toString() + "[" + paramInt + "]");
  }

  public void lock()
  {
  }

  public void cleanup()
  {
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.JSObject
 * JD-Core Version:    0.6.2
 */