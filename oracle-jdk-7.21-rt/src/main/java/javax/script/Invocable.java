package javax.script;

public abstract interface Invocable
{
  public abstract Object invokeMethod(Object paramObject, String paramString, Object[] paramArrayOfObject)
    throws ScriptException, NoSuchMethodException;

  public abstract Object invokeFunction(String paramString, Object[] paramArrayOfObject)
    throws ScriptException, NoSuchMethodException;

  public abstract <T> T getInterface(Class<T> paramClass);

  public abstract <T> T getInterface(Object paramObject, Class<T> paramClass);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.script.Invocable
 * JD-Core Version:    0.6.2
 */