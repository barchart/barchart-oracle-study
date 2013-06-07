package java.lang;

import java.io.IOException;

public abstract interface Appendable
{
  public abstract Appendable append(CharSequence paramCharSequence)
    throws IOException;

  public abstract Appendable append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
    throws IOException;

  public abstract Appendable append(char paramChar)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Appendable
 * JD-Core Version:    0.6.2
 */