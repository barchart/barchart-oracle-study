package javax.swing.event;

import java.util.EventListener;

public abstract interface TableColumnModelListener extends EventListener
{
  public abstract void columnAdded(TableColumnModelEvent paramTableColumnModelEvent);

  public abstract void columnRemoved(TableColumnModelEvent paramTableColumnModelEvent);

  public abstract void columnMoved(TableColumnModelEvent paramTableColumnModelEvent);

  public abstract void columnMarginChanged(ChangeEvent paramChangeEvent);

  public abstract void columnSelectionChanged(ListSelectionEvent paramListSelectionEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.TableColumnModelListener
 * JD-Core Version:    0.6.2
 */