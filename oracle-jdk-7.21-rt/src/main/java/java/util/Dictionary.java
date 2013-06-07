package java.util;

public abstract class Dictionary<K, V>
{
  public abstract int size();

  public abstract boolean isEmpty();

  public abstract Enumeration<K> keys();

  public abstract Enumeration<V> elements();

  public abstract V get(Object paramObject);

  public abstract V put(K paramK, V paramV);

  public abstract V remove(Object paramObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.Dictionary
 * JD-Core Version:    0.6.2
 */