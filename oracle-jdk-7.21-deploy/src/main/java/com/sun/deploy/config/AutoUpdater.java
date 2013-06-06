package com.sun.deploy.config;

import com.sun.deploy.Environment;

public class AutoUpdater
{
  private boolean done = false;
  private boolean enabled = (Config.getBooleanProperty("deployment.macosx.check.update")) && (!"false".equals(Environment.getenv("JAVA_AUTOUPDATE")));

  public synchronized void checkForUpdate(String[] paramArrayOfString)
  {
    checkForUpdate(paramArrayOfString, false);
  }

  public synchronized void checkForUpdate(String[] paramArrayOfString, boolean paramBoolean)
  {
    if (((!this.done) && (this.enabled)) || (paramBoolean))
    {
      this.done = true;
      initiateUpdateCheck(paramArrayOfString);
    }
  }

  protected void initiateUpdateCheck(String[] paramArrayOfString)
  {
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.config.AutoUpdater
 * JD-Core Version:    0.6.2
 */