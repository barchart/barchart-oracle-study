package javax.management.remote;

import javax.security.auth.Subject;

public abstract interface JMXAuthenticator
{
  public abstract Subject authenticate(Object paramObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.JMXAuthenticator
 * JD-Core Version:    0.6.2
 */