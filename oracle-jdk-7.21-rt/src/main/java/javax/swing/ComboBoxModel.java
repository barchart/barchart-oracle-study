package javax.swing;

public abstract interface ComboBoxModel<E> extends ListModel<E>
{
  public abstract void setSelectedItem(Object paramObject);

  public abstract Object getSelectedItem();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.ComboBoxModel
 * JD-Core Version:    0.6.2
 */