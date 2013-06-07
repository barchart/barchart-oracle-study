package com.sun.jmx.snmp.tasks;

public abstract interface Task extends Runnable
{
  public abstract void cancel();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.tasks.Task
 * JD-Core Version:    0.6.2
 */