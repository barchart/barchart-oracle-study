package sun.swing.text;

import java.awt.print.Printable;

public abstract interface CountingPrintable extends Printable
{
  public abstract int getNumberOfPages();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.text.CountingPrintable
 * JD-Core Version:    0.6.2
 */