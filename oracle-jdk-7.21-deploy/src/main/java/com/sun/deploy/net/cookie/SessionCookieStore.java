package com.sun.deploy.net.cookie;

final class SessionCookieStore extends CookieStore
{
  protected void loadCookieJar()
  {
  }

  protected void saveCookieJar()
  {
  }

  protected String getName()
  {
    return "Session Cookie Store";
  }

  protected boolean shouldRejectCookie(HttpCookie paramHttpCookie)
  {
    if (super.shouldRejectCookie(paramHttpCookie))
      return true;
    return paramHttpCookie.getExpirationDate() != null;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.cookie.SessionCookieStore
 * JD-Core Version:    0.6.2
 */