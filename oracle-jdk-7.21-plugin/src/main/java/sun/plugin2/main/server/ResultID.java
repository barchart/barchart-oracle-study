package sun.plugin2.main.server;

public class ResultID
{
  private int id;

  public ResultID(int paramInt)
  {
    this.id = paramInt;
  }

  public int getID()
  {
    return this.id;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (getClass() != paramObject.getClass()))
      return false;
    return this.id == ((ResultID)paramObject).id;
  }

  public int hashCode()
  {
    return this.id;
  }

  public String toString()
  {
    return "[ResultID " + this.id + "]";
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.ResultID
 * JD-Core Version:    0.6.2
 */