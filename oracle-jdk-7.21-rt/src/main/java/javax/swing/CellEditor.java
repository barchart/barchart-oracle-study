package javax.swing;

import java.util.EventObject;
import javax.swing.event.CellEditorListener;

public abstract interface CellEditor
{
  public abstract Object getCellEditorValue();

  public abstract boolean isCellEditable(EventObject paramEventObject);

  public abstract boolean shouldSelectCell(EventObject paramEventObject);

  public abstract boolean stopCellEditing();

  public abstract void cancelCellEditing();

  public abstract void addCellEditorListener(CellEditorListener paramCellEditorListener);

  public abstract void removeCellEditorListener(CellEditorListener paramCellEditorListener);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.CellEditor
 * JD-Core Version:    0.6.2
 */