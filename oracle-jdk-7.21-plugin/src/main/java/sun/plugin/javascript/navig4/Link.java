package sun.plugin.javascript.navig4;

import java.util.HashMap;

public class Link extends sun.plugin.javascript.navig.Link
{
  private static HashMap fieldTable = new HashMap();

  protected Link(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, null);
  }

  static
  {
    fieldTable.put("text", Boolean.TRUE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.Link
 * JD-Core Version:    0.6.2
 */