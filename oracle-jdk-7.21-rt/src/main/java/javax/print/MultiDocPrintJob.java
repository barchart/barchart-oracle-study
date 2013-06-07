package javax.print;

import javax.print.attribute.PrintRequestAttributeSet;

public abstract interface MultiDocPrintJob extends DocPrintJob
{
  public abstract void print(MultiDoc paramMultiDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrintException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.MultiDocPrintJob
 * JD-Core Version:    0.6.2
 */