package javax.swing;

import javax.swing.event.ChangeListener;

public abstract interface SpinnerModel
{
  public abstract Object getValue();

  public abstract void setValue(Object paramObject);

  public abstract Object getNextValue();

  public abstract Object getPreviousValue();

  public abstract void addChangeListener(ChangeListener paramChangeListener);

  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.SpinnerModel
 * JD-Core Version:    0.6.2
 */