package java.nio.file.attribute;

public abstract interface FileAttribute<T>
{
  public abstract String name();

  public abstract T value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.file.attribute.FileAttribute
 * JD-Core Version:    0.6.2
 */