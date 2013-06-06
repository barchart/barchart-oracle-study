package com.sun.deploy.util;

public class DeployLock
{
  private Thread lockingThread = null;

  public synchronized boolean lock()
    throws InterruptedException
  {
    boolean bool = false;
    Thread localThread = Thread.currentThread();
    if (this.lockingThread != localThread)
    {
      while ((this.lockingThread != null) && (this.lockingThread != localThread))
        wait();
      bool = true;
    }
    this.lockingThread = localThread;
    return bool;
  }

  public synchronized void unlock()
  {
    if (this.lockingThread != Thread.currentThread())
      throw new IllegalMonitorStateException("Calling thread has not locked this lock");
    this.lockingThread = null;
    notify();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.DeployLock
 * JD-Core Version:    0.6.2
 */