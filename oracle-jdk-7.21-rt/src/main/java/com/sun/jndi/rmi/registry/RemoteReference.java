package com.sun.jndi.rmi.registry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.naming.NamingException;
import javax.naming.Reference;

public abstract interface RemoteReference extends Remote
{
  public abstract Reference getReference()
    throws NamingException, RemoteException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.rmi.registry.RemoteReference
 * JD-Core Version:    0.6.2
 */