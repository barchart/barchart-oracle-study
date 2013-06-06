package com.sun.applet2.preloader.event;

public class UserDeclinedEvent extends PreloaderEvent
{
  String url = null;

  public UserDeclinedEvent(String paramString)
  {
    super(7);
    this.url = paramString;
  }

  public String toString()
  {
    return "UserDeclinedEvent [url=" + this.url + "]";
  }

  public String getLocation()
  {
    return this.url;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.applet2.preloader.event.UserDeclinedEvent
 * JD-Core Version:    0.6.2
 */