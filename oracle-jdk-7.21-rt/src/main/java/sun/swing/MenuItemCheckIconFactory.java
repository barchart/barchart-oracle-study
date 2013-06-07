package sun.swing;

import javax.swing.Icon;
import javax.swing.JMenuItem;

public abstract interface MenuItemCheckIconFactory
{
  public abstract Icon getIcon(JMenuItem paramJMenuItem);

  public abstract boolean isCompatible(Object paramObject, String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.MenuItemCheckIconFactory
 * JD-Core Version:    0.6.2
 */