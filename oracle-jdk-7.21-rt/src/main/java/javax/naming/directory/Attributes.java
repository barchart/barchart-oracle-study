package javax.naming.directory;

import java.io.Serializable;
import javax.naming.NamingEnumeration;

public abstract interface Attributes extends Cloneable, Serializable
{
  public abstract boolean isCaseIgnored();

  public abstract int size();

  public abstract Attribute get(String paramString);

  public abstract NamingEnumeration<? extends Attribute> getAll();

  public abstract NamingEnumeration<String> getIDs();

  public abstract Attribute put(String paramString, Object paramObject);

  public abstract Attribute put(Attribute paramAttribute);

  public abstract Attribute remove(String paramString);

  public abstract Object clone();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.directory.Attributes
 * JD-Core Version:    0.6.2
 */