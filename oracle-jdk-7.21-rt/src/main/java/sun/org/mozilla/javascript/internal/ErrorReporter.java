package sun.org.mozilla.javascript.internal;

public abstract interface ErrorReporter
{
  public abstract void warning(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2);

  public abstract void error(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2);

  public abstract EvaluatorException runtimeError(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.ErrorReporter
 * JD-Core Version:    0.6.2
 */