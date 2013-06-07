package com.sun.java_cup.internal.runtime;

public abstract interface Scanner
{
  public abstract Symbol next_token()
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java_cup.internal.runtime.Scanner
 * JD-Core Version:    0.6.2
 */