package sun.misc;

import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;

public abstract interface JavaxSecurityAuthKerberosAccess
{
  public abstract EncryptionKey[] keyTabGetEncryptionKeys(KeyTab paramKeyTab, PrincipalName paramPrincipalName);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.JavaxSecurityAuthKerberosAccess
 * JD-Core Version:    0.6.2
 */