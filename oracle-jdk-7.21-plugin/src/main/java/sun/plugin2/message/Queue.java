package sun.plugin2.message;

import java.util.LinkedList;

class Queue
{
  private LinkedList messages = new LinkedList();
  private Thread waiter;

  public synchronized void put(Message paramMessage)
  {
    this.messages.add(paramMessage);
    notifyAll();
  }

  public synchronized Message get()
  {
    if (this.messages.size() == 0)
      return null;
    return (Message)this.messages.removeFirst();
  }

  public synchronized Message get(int paramInt, Conversation paramConversation)
  {
    for (int i = 0; i < this.messages.size(); i++)
    {
      Message localMessage = (Message)this.messages.get(i);
      if (((paramInt < 0) || (localMessage.getID() == paramInt)) && ((paramConversation == null) || (paramConversation.equals(localMessage.getConversation()))))
      {
        this.messages.remove(i);
        return localMessage;
      }
    }
    return null;
  }

  public synchronized Message waitForMessage(long paramLong)
    throws InterruptedException
  {
    if (this.messages.size() == 0)
    {
      this.waiter = Thread.currentThread();
      try
      {
        wait(paramLong);
      }
      finally
      {
        this.waiter = null;
      }
    }
    return get();
  }

  public synchronized Message waitForMessage(long paramLong, int paramInt, Conversation paramConversation)
    throws InterruptedException
  {
    Message localMessage = get(paramInt, paramConversation);
    if (localMessage != null)
      return localMessage;
    int i = paramLong == 0L ? 1 : 0;
    do
    {
      long l1 = System.currentTimeMillis();
      this.waiter = Thread.currentThread();
      try
      {
        wait(paramLong);
      }
      finally
      {
        this.waiter = null;
      }
      long l2 = System.currentTimeMillis();
      if (i == 0)
        paramLong -= Math.max(0L, l2 - l1);
      localMessage = get(paramInt, paramConversation);
    }
    while ((localMessage == null) && ((i != 0) || (paramLong > 0L)));
    return localMessage;
  }

  public synchronized void interrupt()
  {
    if (this.waiter != null)
      this.waiter.interrupt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.Queue
 * JD-Core Version:    0.6.2
 */