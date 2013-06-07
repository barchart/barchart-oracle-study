package java.nio.file.attribute;

import java.io.IOException;

public abstract class UserPrincipalLookupService
{
  public abstract UserPrincipal lookupPrincipalByName(String paramString)
    throws IOException;

  public abstract GroupPrincipal lookupPrincipalByGroupName(String paramString)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.file.attribute.UserPrincipalLookupService
 * JD-Core Version:    0.6.2
 */