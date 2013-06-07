package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

@Taxonomy(stability=Stability.UNCOMMITTED)
public abstract interface BootAMXMBean
{
  public static final String BOOT_AMX_OPERATION_NAME = "bootAMX";

  public abstract ObjectName bootAMX();

  public abstract JMXServiceURL[] getJMXServiceURLs();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.amx.BootAMXMBean
 * JD-Core Version:    0.6.2
 */