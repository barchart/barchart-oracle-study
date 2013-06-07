package org.omg.CORBA;

public abstract class NVList
{
  public abstract int count();

  public abstract NamedValue add(int paramInt);

  public abstract NamedValue add_item(String paramString, int paramInt);

  public abstract NamedValue add_value(String paramString, Any paramAny, int paramInt);

  public abstract NamedValue item(int paramInt)
    throws Bounds;

  public abstract void remove(int paramInt)
    throws Bounds;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.NVList
 * JD-Core Version:    0.6.2
 */