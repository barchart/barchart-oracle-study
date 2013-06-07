package java.awt;

import java.awt.event.ItemListener;

public abstract interface ItemSelectable
{
  public abstract Object[] getSelectedObjects();

  public abstract void addItemListener(ItemListener paramItemListener);

  public abstract void removeItemListener(ItemListener paramItemListener);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.ItemSelectable
 * JD-Core Version:    0.6.2
 */