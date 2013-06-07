package javax.smartcardio;

import java.nio.ByteBuffer;

public abstract class CardChannel
{
  public abstract Card getCard();

  public abstract int getChannelNumber();

  public abstract ResponseAPDU transmit(CommandAPDU paramCommandAPDU)
    throws CardException;

  public abstract int transmit(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
    throws CardException;

  public abstract void close()
    throws CardException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.smartcardio.CardChannel
 * JD-Core Version:    0.6.2
 */