package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;

public abstract interface CorbaContactInfoList extends ContactInfoList
{
  public abstract void setTargetIOR(IOR paramIOR);

  public abstract IOR getTargetIOR();

  public abstract void setEffectiveTargetIOR(IOR paramIOR);

  public abstract IOR getEffectiveTargetIOR();

  public abstract LocalClientRequestDispatcher getLocalClientRequestDispatcher();

  public abstract int hashCode();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.transport.CorbaContactInfoList
 * JD-Core Version:    0.6.2
 */