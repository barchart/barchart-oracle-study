package java.nio.file.attribute;

import java.io.IOException;

public abstract interface FileOwnerAttributeView extends FileAttributeView
{
  public abstract String name();

  public abstract UserPrincipal getOwner()
    throws IOException;

  public abstract void setOwner(UserPrincipal paramUserPrincipal)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.file.attribute.FileOwnerAttributeView
 * JD-Core Version:    0.6.2
 */