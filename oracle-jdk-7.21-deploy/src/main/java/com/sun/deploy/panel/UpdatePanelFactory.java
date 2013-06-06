package com.sun.deploy.panel;

public class UpdatePanelFactory
{
  static UpdatePanelImpl sUpdatePanelImpl = new UnixUpdatePanelImpl();

  static UpdatePanelImpl getInstance()
  {
    return sUpdatePanelImpl;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.UpdatePanelFactory
 * JD-Core Version:    0.6.2
 */