package sun.plugin2.message.transport;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.Message;
import sun.plugin2.message.Serializer;

public abstract class SerializingTransport
  implements Transport
{
  private Map messageIDMap = new HashMap();
  private final Object writeLock = new Object();

  public void registerMessageID(int paramInt, Class paramClass)
    throws IllegalArgumentException
  {
    Integer localInteger = new Integer(paramInt);
    if (this.messageIDMap.get(localInteger) != null)
      throw new IllegalArgumentException("Message ID " + paramInt + " already registered");
    try
    {
      Constructor localConstructor = paramClass.getConstructor(new Class[] { Conversation.class });
      this.messageIDMap.put(localInteger, localConstructor);
    }
    catch (Exception localException)
    {
      throw ((IllegalArgumentException)new IllegalArgumentException().initCause(localException));
    }
  }

  public void write(Message paramMessage)
    throws IOException
  {
    synchronized (this.writeLock)
    {
      Serializer localSerializer = getSerializer();
      localSerializer.writeInt(paramMessage.getID());
      localSerializer.writeConversation(paramMessage.getConversation());
      paramMessage.writeFields(localSerializer);
      signalDataWritten();
    }
  }

  public Message read()
    throws IOException
  {
    if (!isDataAvailable())
      return null;
    Serializer localSerializer = getSerializer();
    int i = localSerializer.readInt();
    Constructor localConstructor = (Constructor)this.messageIDMap.get(new Integer(i));
    if (localConstructor == null)
      throw new IOException("Unregistered message ID " + i);
    Conversation localConversation = localSerializer.readConversation();
    Message localMessage = null;
    try
    {
      localMessage = (Message)localConstructor.newInstance(new Object[] { localConversation });
    }
    catch (Exception localException)
    {
      throw ((IOException)new IOException().initCause(localException));
    }
    localMessage.readFields(localSerializer);
    signalDataRead();
    return localMessage;
  }

  public abstract void waitForData(long paramLong)
    throws IOException;

  protected abstract void signalDataWritten()
    throws IOException;

  protected abstract void signalDataRead();

  protected abstract boolean isDataAvailable()
    throws IOException;

  protected abstract Serializer getSerializer();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.transport.SerializingTransport
 * JD-Core Version:    0.6.2
 */