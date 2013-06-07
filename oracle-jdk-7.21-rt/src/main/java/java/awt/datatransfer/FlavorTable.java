package java.awt.datatransfer;

import java.util.List;

public abstract interface FlavorTable extends FlavorMap
{
  public abstract List<String> getNativesForFlavor(DataFlavor paramDataFlavor);

  public abstract List<DataFlavor> getFlavorsForNative(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.datatransfer.FlavorTable
 * JD-Core Version:    0.6.2
 */