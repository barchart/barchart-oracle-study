package javax.xml.ws;

public abstract interface AsyncHandler<T>
{
  public abstract void handleResponse(Response<T> paramResponse);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.AsyncHandler
 * JD-Core Version:    0.6.2
 */