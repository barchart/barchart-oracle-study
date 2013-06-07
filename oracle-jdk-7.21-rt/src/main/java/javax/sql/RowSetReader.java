package javax.sql;

import java.sql.SQLException;

public abstract interface RowSetReader
{
  public abstract void readData(RowSetInternal paramRowSetInternal)
    throws SQLException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.RowSetReader
 * JD-Core Version:    0.6.2
 */