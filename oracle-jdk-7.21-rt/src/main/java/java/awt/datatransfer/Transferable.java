package java.awt.datatransfer;

import java.io.IOException;

public abstract interface Transferable
{
  public abstract DataFlavor[] getTransferDataFlavors();

  public abstract boolean isDataFlavorSupported(DataFlavor paramDataFlavor);

  public abstract Object getTransferData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.datatransfer.Transferable
 * JD-Core Version:    0.6.2
 */