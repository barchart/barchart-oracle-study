package sun.dc.path;

public abstract interface FastPathProducer
{
  public abstract void getBox(float[] paramArrayOfFloat)
    throws PathError;

  public abstract void sendTo(PathConsumer paramPathConsumer)
    throws PathError, PathException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.dc.path.FastPathProducer
 * JD-Core Version:    0.6.2
 */