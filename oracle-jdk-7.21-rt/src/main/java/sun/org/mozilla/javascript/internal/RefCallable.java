package sun.org.mozilla.javascript.internal;

public abstract interface RefCallable extends Callable
{
  public abstract Ref refCall(Context paramContext, Scriptable paramScriptable, Object[] paramArrayOfObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.RefCallable
 * JD-Core Version:    0.6.2
 */