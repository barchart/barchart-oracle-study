package javax.lang.model.type;

public abstract interface WildcardType extends TypeMirror
{
  public abstract TypeMirror getExtendsBound();

  public abstract TypeMirror getSuperBound();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.type.WildcardType
 * JD-Core Version:    0.6.2
 */