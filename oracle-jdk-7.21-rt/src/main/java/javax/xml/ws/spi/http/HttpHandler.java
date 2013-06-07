package javax.xml.ws.spi.http;

import java.io.IOException;

public abstract class HttpHandler
{
  public abstract void handle(HttpExchange paramHttpExchange)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.spi.http.HttpHandler
 * JD-Core Version:    0.6.2
 */