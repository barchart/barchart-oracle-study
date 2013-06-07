package com.sun.corba.se.spi.ior;

public abstract interface TaggedProfile extends Identifiable, MakeImmutable
{
  public abstract TaggedProfileTemplate getTaggedProfileTemplate();

  public abstract ObjectId getObjectId();

  public abstract ObjectKeyTemplate getObjectKeyTemplate();

  public abstract ObjectKey getObjectKey();

  public abstract boolean isEquivalent(TaggedProfile paramTaggedProfile);

  public abstract org.omg.IOP.TaggedProfile getIOPProfile();

  public abstract boolean isLocal();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.TaggedProfile
 * JD-Core Version:    0.6.2
 */