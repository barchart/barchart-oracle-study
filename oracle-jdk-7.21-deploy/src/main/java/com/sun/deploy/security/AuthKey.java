package com.sun.deploy.security;

public abstract interface AuthKey
{
  public abstract boolean isProxy();

  public abstract String getProtocolScheme();

  public abstract String getHost();

  public abstract int getPort();

  public abstract String getPath();

  public abstract String getScheme();

  public abstract String getPrompt();
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.AuthKey
 * JD-Core Version:    0.6.2
 */