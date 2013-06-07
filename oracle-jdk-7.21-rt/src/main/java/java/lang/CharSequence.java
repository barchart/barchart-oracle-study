package java.lang;

public abstract interface CharSequence
{
  public abstract int length();

  public abstract char charAt(int paramInt);

  public abstract CharSequence subSequence(int paramInt1, int paramInt2);

  public abstract String toString();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.CharSequence
 * JD-Core Version:    0.6.2
 */