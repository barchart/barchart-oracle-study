package java.awt;

public abstract interface MenuContainer
{
  public abstract Font getFont();

  public abstract void remove(MenuComponent paramMenuComponent);

  @Deprecated
  public abstract boolean postEvent(Event paramEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.MenuContainer
 * JD-Core Version:    0.6.2
 */