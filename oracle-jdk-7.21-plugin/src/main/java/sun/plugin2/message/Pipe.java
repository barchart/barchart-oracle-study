package sun.plugin2.message;

import com.sun.deploy.Environment;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.Waiter;
import com.sun.deploy.util.Waiter.WaiterTask;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.plugin2.message.transport.Transport;
import sun.plugin2.util.PojoUtil;
import sun.plugin2.util.SystemUtil;

public class Pipe
{
  private static final boolean DEBUG = SystemUtil.isDebug();
  private static final String logDir = Environment.getenv("JPI2_PIPE_LOGDIR");
  private final PrintWriter logger;
  private Transport transport;
  private boolean initiatingSide;
  private static final ThreadLocal perThreadMsgQueue = new ThreadLocal();
  private Queue mainMsgQueue = new Queue();
  private Map activeConversations = new HashMap();
  private int curConversationID;
  private volatile boolean shouldShutdown;
  private volatile boolean shutdownComplete;

  public Pipe(Transport paramTransport, boolean paramBoolean)
  {
    PrintWriter localPrintWriter = null;
    if ((logDir != null) && (!paramBoolean))
    {
      localObject = new File(logDir);
      if ((((File)localObject).isDirectory()) && (((File)localObject).canWrite()))
        try
        {
          File localFile = new File(logDir, "pipe_" + System.currentTimeMillis());
          localFile.createNewFile();
          localPrintWriter = new PrintWriter(new FileWriter(localFile));
          Trace.println("Pipe traffic logged in " + localFile.getAbsolutePath());
        }
        catch (IOException localIOException)
        {
          Trace.println("Failed to setup pipe logger.");
          Trace.ignored(localIOException);
        }
      else
        Trace.println("Ignore pipe log request, JPI2_PIPE_LOGDIR should be a writable directory.");
    }
    this.logger = localPrintWriter;
    this.transport = paramTransport;
    this.initiatingSide = paramBoolean;
    if (DEBUG)
      System.out.println("Pipe.cstr: " + this);
    Object localObject = new WorkerThread();
    ((WorkerThread)localObject).setDaemon(true);
    ((WorkerThread)localObject).start();
  }

  protected void finalize()
    throws Exception
  {
    if (this.logger != null)
      this.logger.close();
  }

  public static boolean isLoggingEnabled()
  {
    return logDir != null;
  }

  private void logMessage(Message paramMessage, boolean paramBoolean)
  {
    if (this.logger != null)
    {
      String str = paramBoolean ? "-> Sent " : "<- Recv ";
      str = str + " at " + System.currentTimeMillis();
      str = str + "\n  by thread[" + Thread.currentThread().getId() + "]" + Thread.currentThread().getName();
      str = str + "\n  " + paramMessage.getClass().getName();
      str = str + "\n" + PojoUtil.toJson(paramMessage);
      this.logger.println(str);
      this.logger.flush();
    }
  }

  public void send(Message paramMessage)
    throws IOException
  {
    logMessage(paramMessage, true);
    this.transport.write(paramMessage);
  }

  public Message poll()
    throws IOException
  {
    checkForShutdown();
    return this.mainMsgQueue.get();
  }

  public Message poll(Conversation paramConversation)
    throws IOException
  {
    checkForShutdown();
    Queue localQueue = (Queue)perThreadMsgQueue.get();
    if (localQueue == null)
      return null;
    return localQueue.get(-1, paramConversation);
  }

  public Message receive(final long paramLong)
    throws InterruptedException, IOException
  {
    checkForShutdown();
    try
    {
      return (Message)Waiter.runAndWait(new Waiter.WaiterTask()
      {
        private final long val$millisToWait;

        public Object run()
          throws InterruptedException
        {
          return Pipe.this.mainMsgQueue.waitForMessage(paramLong);
        }
      });
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      if ((localException instanceof InterruptedException))
        throw ((InterruptedException)localException);
    }
    return null;
  }

  public Message receive(final long paramLong, Conversation paramConversation)
    throws InterruptedException, IOException
  {
    checkForShutdown();
    Queue localQueue1 = (Queue)this.activeConversations.get(paramConversation);
    final Queue localQueue2 = (Queue)perThreadMsgQueue.get();
    if ((localQueue1 == null) || (localQueue1 != localQueue2))
    {
      Trace.ignored(new Throwable("Conversation is not matching thread queue, something is not right..."));
      Trace.println("Conversation " + paramConversation + " bound to queue " + localQueue1, TraceLevel.BASIC);
      Trace.println("Thread " + Thread.currentThread().getName() + " bound to queue " + localQueue2, TraceLevel.BASIC);
    }
    if (localQueue2 == null)
    {
      Trace.ignored(new Throwable("No queue bound to this thread, somthing is not right..."));
      return null;
    }
    try
    {
      return (Message)Waiter.runAndWait(new Waiter.WaiterTask()
      {
        private final Queue val$threadLocalQueue;
        private final long val$millisToWait;
        private final Conversation val$conversation;

        public Object run()
          throws InterruptedException
        {
          return localQueue2.waitForMessage(paramLong, -1, this.val$conversation);
        }
      });
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      if ((localException instanceof InterruptedException))
        throw ((InterruptedException)localException);
    }
    return null;
  }

  public synchronized Conversation beginConversation()
  {
    int i = this.curConversationID++;
    Conversation localConversation = new Conversation(this.initiatingSide, i);
    Queue localQueue = (Queue)perThreadMsgQueue.get();
    if (localQueue == null)
    {
      localQueue = new Queue();
      perThreadMsgQueue.set(localQueue);
    }
    this.activeConversations.put(localConversation, localQueue);
    return localConversation;
  }

  public synchronized boolean joinConversation(Conversation paramConversation)
  {
    Queue localQueue1 = (Queue)this.activeConversations.get(paramConversation);
    Queue localQueue2 = (Queue)perThreadMsgQueue.get();
    if (localQueue1 != null)
      return localQueue1 == localQueue2;
    if (localQueue2 == null)
    {
      localQueue2 = new Queue();
      perThreadMsgQueue.set(localQueue2);
    }
    this.activeConversations.put(paramConversation, localQueue2);
    return true;
  }

  public void endConversation(Conversation paramConversation)
  {
    this.activeConversations.remove(paramConversation);
  }

  public void shutdown()
  {
    this.shouldShutdown = true;
  }

  private synchronized Queue getQueue(Conversation paramConversation)
  {
    return (Queue)this.activeConversations.get(paramConversation);
  }

  private synchronized void interruptActiveQueues()
  {
    this.mainMsgQueue.interrupt();
    Iterator localIterator = this.activeConversations.values().iterator();
    while (localIterator.hasNext())
      ((Queue)localIterator.next()).interrupt();
  }

  public boolean shutdownComplete()
  {
    return this.shutdownComplete;
  }

  private void checkForShutdown()
    throws IOException
  {
    if (this.shutdownComplete)
      throw new IOException("Pipe is already shut down");
  }

  public String toString()
  {
    return "Pipe{transport=" + this.transport + ", initiatingSide=" + this.initiatingSide + '}';
  }

  class WorkerThread extends Thread
  {
    public WorkerThread()
    {
      super();
    }

    public void run()
    {
      try
      {
        while (!Pipe.this.shouldShutdown)
        {
          Message localMessage = null;
          Pipe.this.transport.waitForData(500L);
          while ((localMessage = Pipe.this.transport.read()) != null)
          {
            Pipe.this.logMessage(localMessage, false);
            Conversation localConversation = localMessage.getConversation();
            int i = 0;
            if (localConversation != null)
            {
              Queue localQueue = Pipe.this.getQueue(localConversation);
              if (localQueue != null)
              {
                localQueue.put(localMessage);
                i = 1;
              }
            }
            if (i == 0)
              Pipe.this.mainMsgQueue.put(localMessage);
          }
        }
      }
      catch (IOException localIOException)
      {
        Pipe.this.interruptActiveQueues();
        if (Pipe.DEBUG)
        {
          System.out.println("Terminating " + Thread.currentThread().getName() + " due to exception:");
          localIOException.printStackTrace();
        }
      }
      finally
      {
        jsr 6;
      }
      Pipe.this.shutdownComplete = true;
      synchronized (Pipe.this)
      {
        Pipe.this.notifyAll();
      }
      ret;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.Pipe
 * JD-Core Version:    0.6.2
 */