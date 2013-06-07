package javax.swing.colorchooser;

import java.awt.Color;
import javax.swing.event.ChangeListener;

public abstract interface ColorSelectionModel
{
  public abstract Color getSelectedColor();

  public abstract void setSelectedColor(Color paramColor);

  public abstract void addChangeListener(ChangeListener paramChangeListener);

  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.colorchooser.ColorSelectionModel
 * JD-Core Version:    0.6.2
 */