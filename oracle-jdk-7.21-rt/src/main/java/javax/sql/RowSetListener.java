package javax.sql;

import java.util.EventListener;

public abstract interface RowSetListener extends EventListener
{
  public abstract void rowSetChanged(RowSetEvent paramRowSetEvent);

  public abstract void rowChanged(RowSetEvent paramRowSetEvent);

  public abstract void cursorMoved(RowSetEvent paramRowSetEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.RowSetListener
 * JD-Core Version:    0.6.2
 */