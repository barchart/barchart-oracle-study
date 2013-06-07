package javax.swing;

import javax.swing.event.ListDataListener;

public abstract interface ListModel<E>
{
  public abstract int getSize();

  public abstract E getElementAt(int paramInt);

  public abstract void addListDataListener(ListDataListener paramListDataListener);

  public abstract void removeListDataListener(ListDataListener paramListDataListener);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.ListModel
 * JD-Core Version:    0.6.2
 */