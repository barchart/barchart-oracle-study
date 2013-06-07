package javax.swing;

import java.awt.Component;
import java.awt.event.ActionListener;

public abstract interface ComboBoxEditor
{
  public abstract Component getEditorComponent();

  public abstract void setItem(Object paramObject);

  public abstract Object getItem();

  public abstract void selectAll();

  public abstract void addActionListener(ActionListener paramActionListener);

  public abstract void removeActionListener(ActionListener paramActionListener);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.ComboBoxEditor
 * JD-Core Version:    0.6.2
 */