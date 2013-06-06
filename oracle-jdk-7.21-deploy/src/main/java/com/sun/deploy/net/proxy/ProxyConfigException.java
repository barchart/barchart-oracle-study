package com.sun.deploy.net.proxy;

public final class ProxyConfigException extends Exception
{
  private String _msg = null;
  private Throwable _cause = null;

  public ProxyConfigException()
  {
  }

  public ProxyConfigException(String paramString)
  {
    super(paramString);
    this._msg = paramString;
  }

  public ProxyConfigException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    this._msg = paramString;
    this._cause = paramThrowable;
    paramThrowable.printStackTrace();
  }

  public String toString()
  {
    return "ProxyConfigException: " + this._msg + " , " + this._cause;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.proxy.ProxyConfigException
 * JD-Core Version:    0.6.2
 */