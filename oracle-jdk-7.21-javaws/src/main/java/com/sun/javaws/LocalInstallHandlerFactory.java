package com.sun.javaws;

import com.sun.deploy.association.utility.GnomeAssociationUtil;
import com.sun.javaws.xdg.XDGInstallHandler;

public class LocalInstallHandlerFactory
{
  public static LocalInstallHandler newInstance()
  {
    if (GnomeAssociationUtil.supportsCurrentPlatform())
      return new OldGnomeInstallHandler();
    return new XDGInstallHandler();
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.LocalInstallHandlerFactory
 * JD-Core Version:    0.6.2
 */