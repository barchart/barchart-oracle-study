package javax.sql;

import java.sql.SQLException;

public abstract interface RowSetWriter
{
  public abstract boolean writeData(RowSetInternal paramRowSetInternal)
    throws SQLException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.RowSetWriter
 * JD-Core Version:    0.6.2
 */