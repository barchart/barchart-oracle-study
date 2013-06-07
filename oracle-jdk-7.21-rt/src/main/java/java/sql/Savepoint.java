package java.sql;

public abstract interface Savepoint
{
  public abstract int getSavepointId()
    throws SQLException;

  public abstract String getSavepointName()
    throws SQLException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.sql.Savepoint
 * JD-Core Version:    0.6.2
 */