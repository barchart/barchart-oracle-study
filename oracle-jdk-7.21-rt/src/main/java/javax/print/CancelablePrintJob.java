package javax.print;

public abstract interface CancelablePrintJob extends DocPrintJob
{
  public abstract void cancel()
    throws PrintException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.CancelablePrintJob
 * JD-Core Version:    0.6.2
 */