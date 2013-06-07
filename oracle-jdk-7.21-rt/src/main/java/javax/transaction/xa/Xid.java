package javax.transaction.xa;

public abstract interface Xid
{
  public static final int MAXGTRIDSIZE = 64;
  public static final int MAXBQUALSIZE = 64;

  public abstract int getFormatId();

  public abstract byte[] getGlobalTransactionId();

  public abstract byte[] getBranchQualifier();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.transaction.xa.Xid
 * JD-Core Version:    0.6.2
 */