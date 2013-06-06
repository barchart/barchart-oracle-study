package com.sun.deploy.perf;

import java.io.PrintStream;

public abstract interface PerfRollup
{
  public abstract void doRollup(PerfLabel[] paramArrayOfPerfLabel, PrintStream paramPrintStream);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.perf.PerfRollup
 * JD-Core Version:    0.6.2
 */