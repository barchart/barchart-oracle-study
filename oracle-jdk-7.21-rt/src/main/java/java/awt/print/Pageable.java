package java.awt.print;

public abstract interface Pageable
{
  public static final int UNKNOWN_NUMBER_OF_PAGES = -1;

  public abstract int getNumberOfPages();

  public abstract PageFormat getPageFormat(int paramInt)
    throws IndexOutOfBoundsException;

  public abstract Printable getPrintable(int paramInt)
    throws IndexOutOfBoundsException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.print.Pageable
 * JD-Core Version:    0.6.2
 */