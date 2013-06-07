package com.sun.jndi.ldap.pool;

public abstract interface PoolCallback
{
  public abstract boolean releasePooledConnection(PooledConnection paramPooledConnection);

  public abstract boolean removePooledConnection(PooledConnection paramPooledConnection);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.pool.PoolCallback
 * JD-Core Version:    0.6.2
 */