package com.sun.deploy.net.offline;

import java.net.URL;

public abstract interface OfflineHandler
{
  public abstract boolean isGlobalOffline();

  public abstract boolean setGlobalOffline(boolean paramBoolean);

  public abstract boolean askUserGoOnline(URL paramURL);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.offline.OfflineHandler
 * JD-Core Version:    0.6.2
 */