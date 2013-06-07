package java.awt;

public abstract interface LayoutManager2 extends LayoutManager
{
  public abstract void addLayoutComponent(Component paramComponent, Object paramObject);

  public abstract Dimension maximumLayoutSize(Container paramContainer);

  public abstract float getLayoutAlignmentX(Container paramContainer);

  public abstract float getLayoutAlignmentY(Container paramContainer);

  public abstract void invalidateLayout(Container paramContainer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.LayoutManager2
 * JD-Core Version:    0.6.2
 */