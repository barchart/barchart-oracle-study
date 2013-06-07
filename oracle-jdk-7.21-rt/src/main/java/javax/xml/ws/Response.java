package javax.xml.ws;

import java.util.Map;
import java.util.concurrent.Future;

public abstract interface Response<T> extends Future<T>
{
  public abstract Map<String, Object> getContext();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.Response
 * JD-Core Version:    0.6.2
 */