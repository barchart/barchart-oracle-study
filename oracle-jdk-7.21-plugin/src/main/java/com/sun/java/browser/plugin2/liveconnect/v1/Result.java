package com.sun.java.browser.plugin2.liveconnect.v1;

public final class Result
{
  private Object value;
  private boolean skipUnboxing;

  public Result(Object paramObject, boolean paramBoolean)
  {
    this.value = paramObject;
    this.skipUnboxing = paramBoolean;
  }

  public Object value()
  {
    return this.value;
  }

  public boolean skipUnboxing()
  {
    return this.skipUnboxing;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.java.browser.plugin2.liveconnect.v1.Result
 * JD-Core Version:    0.6.2
 */