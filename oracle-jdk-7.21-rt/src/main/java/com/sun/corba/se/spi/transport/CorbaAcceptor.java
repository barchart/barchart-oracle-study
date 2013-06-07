package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.IORTemplate;

public abstract interface CorbaAcceptor extends Acceptor
{
  public abstract String getObjectAdapterId();

  public abstract String getObjectAdapterManagerId();

  public abstract void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString);

  public abstract String getMonitoringName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.transport.CorbaAcceptor
 * JD-Core Version:    0.6.2
 */