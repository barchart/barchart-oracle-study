package sun.print;

import javax.print.PrintService;

public abstract interface BackgroundLookupListener
{
  public abstract void notifyServices(PrintService[] paramArrayOfPrintService);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.BackgroundLookupListener
 * JD-Core Version:    0.6.2
 */