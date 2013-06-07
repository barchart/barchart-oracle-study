package sun.org.mozilla.javascript.internal;

public abstract interface Function extends Scriptable, Callable
{
  public abstract Object call(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject);

  public abstract Scriptable construct(Context paramContext, Scriptable paramScriptable, Object[] paramArrayOfObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.Function
 * JD-Core Version:    0.6.2
 */