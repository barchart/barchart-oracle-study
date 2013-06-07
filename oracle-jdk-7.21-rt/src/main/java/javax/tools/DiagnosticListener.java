package javax.tools;

public abstract interface DiagnosticListener<S>
{
  public abstract void report(Diagnostic<? extends S> paramDiagnostic);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.tools.DiagnosticListener
 * JD-Core Version:    0.6.2
 */