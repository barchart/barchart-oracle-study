package java.nio.file.attribute;

import java.io.IOException;
import java.util.Set;

public abstract interface PosixFileAttributeView extends BasicFileAttributeView, FileOwnerAttributeView
{
  public abstract String name();

  public abstract PosixFileAttributes readAttributes()
    throws IOException;

  public abstract void setPermissions(Set<PosixFilePermission> paramSet)
    throws IOException;

  public abstract void setGroup(GroupPrincipal paramGroupPrincipal)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.file.attribute.PosixFileAttributeView
 * JD-Core Version:    0.6.2
 */