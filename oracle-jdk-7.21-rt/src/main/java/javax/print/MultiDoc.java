package javax.print;

import java.io.IOException;

public abstract interface MultiDoc
{
  public abstract Doc getDoc()
    throws IOException;

  public abstract MultiDoc next()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.MultiDoc
 * JD-Core Version:    0.6.2
 */