package java.awt;

public abstract interface Transparency
{
  public static final int OPAQUE = 1;
  public static final int BITMASK = 2;
  public static final int TRANSLUCENT = 3;

  public abstract int getTransparency();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.Transparency
 * JD-Core Version:    0.6.2
 */