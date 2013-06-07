package javax.smartcardio;

public abstract class CardTerminal
{
  public abstract String getName();

  public abstract Card connect(String paramString)
    throws CardException;

  public abstract boolean isCardPresent()
    throws CardException;

  public abstract boolean waitForCardPresent(long paramLong)
    throws CardException;

  public abstract boolean waitForCardAbsent(long paramLong)
    throws CardException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.smartcardio.CardTerminal
 * JD-Core Version:    0.6.2
 */