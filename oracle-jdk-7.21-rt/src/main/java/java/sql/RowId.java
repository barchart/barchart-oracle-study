package java.sql;

public abstract interface RowId
{
  public abstract boolean equals(Object paramObject);

  public abstract byte[] getBytes();

  public abstract String toString();

  public abstract int hashCode();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.sql.RowId
 * JD-Core Version:    0.6.2
 */