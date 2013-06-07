package com.sun.net.httpserver;

import javax.net.ssl.SSLSession;

public abstract class HttpsExchange extends HttpExchange
{
  public abstract SSLSession getSSLSession();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.net.httpserver.HttpsExchange
 * JD-Core Version:    0.6.2
 */