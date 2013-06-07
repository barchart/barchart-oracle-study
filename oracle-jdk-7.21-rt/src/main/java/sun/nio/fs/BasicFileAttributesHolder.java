package sun.nio.fs;

import java.nio.file.attribute.BasicFileAttributes;

public abstract interface BasicFileAttributesHolder
{
  public abstract BasicFileAttributes get();

  public abstract void invalidate();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.BasicFileAttributesHolder
 * JD-Core Version:    0.6.2
 */