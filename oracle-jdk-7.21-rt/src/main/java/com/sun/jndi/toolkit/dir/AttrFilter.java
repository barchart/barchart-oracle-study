package com.sun.jndi.toolkit.dir;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public abstract interface AttrFilter
{
  public abstract boolean check(Attributes paramAttributes)
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.toolkit.dir.AttrFilter
 * JD-Core Version:    0.6.2
 */