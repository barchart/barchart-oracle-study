package java.awt;

public abstract interface LayoutManager
{
  public abstract void addLayoutComponent(String paramString, Component paramComponent);

  public abstract void removeLayoutComponent(Component paramComponent);

  public abstract Dimension preferredLayoutSize(Container paramContainer);

  public abstract Dimension minimumLayoutSize(Container paramContainer);

  public abstract void layoutContainer(Container paramContainer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.LayoutManager
 * JD-Core Version:    0.6.2
 */