package com.sun.deploy.net.protocol.rmi;

import java.util.Vector;
import sun.rmi.transport.proxy.RMIHttpToCGISocketFactory;
import sun.rmi.transport.proxy.RMIHttpToPortSocketFactory;
import sun.rmi.transport.proxy.RMIMasterSocketFactory;

public class DeployRMISocketFactory extends RMIMasterSocketFactory
{
  public DeployRMISocketFactory()
  {
    this.altFactoryList.addElement(new RMIHttpToPortSocketFactory());
    this.altFactoryList.addElement(new RMIHttpToCGISocketFactory());
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.protocol.rmi.DeployRMISocketFactory
 * JD-Core Version:    0.6.2
 */