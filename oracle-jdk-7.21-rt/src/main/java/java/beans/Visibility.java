package java.beans;

public abstract interface Visibility
{
  public abstract boolean needsGui();

  public abstract void dontUseGui();

  public abstract void okToUseGui();

  public abstract boolean avoidingGui();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.Visibility
 * JD-Core Version:    0.6.2
 */