package java.awt.image;

public abstract interface ImageProducer
{
  public abstract void addConsumer(ImageConsumer paramImageConsumer);

  public abstract boolean isConsumer(ImageConsumer paramImageConsumer);

  public abstract void removeConsumer(ImageConsumer paramImageConsumer);

  public abstract void startProduction(ImageConsumer paramImageConsumer);

  public abstract void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.image.ImageProducer
 * JD-Core Version:    0.6.2
 */