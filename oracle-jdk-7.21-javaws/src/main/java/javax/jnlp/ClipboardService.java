package javax.jnlp;

import java.awt.datatransfer.Transferable;

public abstract interface ClipboardService
{
  public abstract Transferable getContents();

  public abstract void setContents(Transferable paramTransferable);
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     javax.jnlp.ClipboardService
 * JD-Core Version:    0.6.2
 */