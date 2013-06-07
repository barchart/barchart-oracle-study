package java.nio.file.attribute;

public abstract interface DosFileAttributes extends BasicFileAttributes
{
  public abstract boolean isReadOnly();

  public abstract boolean isHidden();

  public abstract boolean isArchive();

  public abstract boolean isSystem();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.file.attribute.DosFileAttributes
 * JD-Core Version:    0.6.2
 */