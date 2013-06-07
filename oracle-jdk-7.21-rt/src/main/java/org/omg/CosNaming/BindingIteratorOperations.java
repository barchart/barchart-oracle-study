package org.omg.CosNaming;

public abstract interface BindingIteratorOperations
{
  public abstract boolean next_one(BindingHolder paramBindingHolder);

  public abstract boolean next_n(int paramInt, BindingListHolder paramBindingListHolder);

  public abstract void destroy();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CosNaming.BindingIteratorOperations
 * JD-Core Version:    0.6.2
 */