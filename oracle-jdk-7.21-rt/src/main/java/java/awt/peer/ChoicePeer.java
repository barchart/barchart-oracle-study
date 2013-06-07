package java.awt.peer;

public abstract interface ChoicePeer extends ComponentPeer
{
  public abstract void add(String paramString, int paramInt);

  public abstract void remove(int paramInt);

  public abstract void removeAll();

  public abstract void select(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.ChoicePeer
 * JD-Core Version:    0.6.2
 */