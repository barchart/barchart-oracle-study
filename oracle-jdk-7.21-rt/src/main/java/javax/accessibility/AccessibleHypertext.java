package javax.accessibility;

public abstract interface AccessibleHypertext extends AccessibleText
{
  public abstract int getLinkCount();

  public abstract AccessibleHyperlink getLink(int paramInt);

  public abstract int getLinkIndex(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.accessibility.AccessibleHypertext
 * JD-Core Version:    0.6.2
 */