package javax.naming;

public abstract interface NameParser
{
  public abstract Name parse(String paramString)
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.NameParser
 * JD-Core Version:    0.6.2
 */