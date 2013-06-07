package com.sun.jndi.ldap.pool;

import javax.naming.NamingException;

public abstract interface PooledConnectionFactory
{
  public abstract PooledConnection createPooledConnection(PoolCallback paramPoolCallback)
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.pool.PooledConnectionFactory
 * JD-Core Version:    0.6.2
 */