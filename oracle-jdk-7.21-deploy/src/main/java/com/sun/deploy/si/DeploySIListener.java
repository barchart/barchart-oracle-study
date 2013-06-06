package com.sun.deploy.si;

public abstract interface DeploySIListener
{
  public abstract void newActivation(String[] paramArrayOfString);

  public abstract Object getSingleInstanceListener();
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.si.DeploySIListener
 * JD-Core Version:    0.6.2
 */