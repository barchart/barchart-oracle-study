package javax.smartcardio;

public abstract class Card
{
  public abstract ATR getATR();

  public abstract String getProtocol();

  public abstract CardChannel getBasicChannel();

  public abstract CardChannel openLogicalChannel()
    throws CardException;

  public abstract void beginExclusive()
    throws CardException;

  public abstract void endExclusive()
    throws CardException;

  public abstract byte[] transmitControlCommand(int paramInt, byte[] paramArrayOfByte)
    throws CardException;

  public abstract void disconnect(boolean paramBoolean)
    throws CardException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.smartcardio.Card
 * JD-Core Version:    0.6.2
 */