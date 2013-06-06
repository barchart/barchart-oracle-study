package com.sun.java.browser.plugin2.liveconnect.v1;

import java.applet.Applet;
import sun.plugin2.main.client.LiveConnectSupport;

public final class BridgeFactory
{
  public static Bridge getBridge(Applet paramApplet)
    throws IllegalArgumentException, SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
      localSecurityManager.checkPermission(new RuntimePermission("liveconnect.accessBridge"));
    return LiveConnectSupport.getBridge(paramApplet);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.java.browser.plugin2.liveconnect.v1.BridgeFactory
 * JD-Core Version:    0.6.2
 */