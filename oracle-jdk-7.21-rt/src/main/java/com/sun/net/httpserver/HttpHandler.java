package com.sun.net.httpserver;

import java.io.IOException;

public abstract interface HttpHandler
{
  public abstract void handle(HttpExchange paramHttpExchange)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.net.httpserver.HttpHandler
 * JD-Core Version:    0.6.2
 */