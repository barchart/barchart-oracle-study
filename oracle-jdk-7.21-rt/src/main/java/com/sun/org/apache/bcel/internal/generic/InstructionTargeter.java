package com.sun.org.apache.bcel.internal.generic;

public abstract interface InstructionTargeter
{
  public abstract boolean containsTarget(InstructionHandle paramInstructionHandle);

  public abstract void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.InstructionTargeter
 * JD-Core Version:    0.6.2
 */