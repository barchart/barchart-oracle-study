package org.omg.CORBA;

@Deprecated
public abstract interface DynUnion extends Object, DynAny
{
  public abstract boolean set_as_default();

  public abstract void set_as_default(boolean paramBoolean);

  public abstract DynAny discriminator();

  public abstract TCKind discriminator_kind();

  public abstract DynAny member();

  public abstract String member_name();

  public abstract void member_name(String paramString);

  public abstract TCKind member_kind();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.DynUnion
 * JD-Core Version:    0.6.2
 */