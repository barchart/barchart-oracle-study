package com.sun.deploy.net.offline;

import java.net.URL;

class NeverOfflineHandler
  implements OfflineHandler
{
  public boolean isGlobalOffline()
  {
    return false;
  }

  public boolean setGlobalOffline(boolean paramBoolean)
  {
    return !paramBoolean;
  }

  public boolean askUserGoOnline(URL paramURL)
  {
    return true;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.offline.NeverOfflineHandler
 * JD-Core Version:    0.6.2
 */