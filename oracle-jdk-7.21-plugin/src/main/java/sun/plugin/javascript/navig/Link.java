package sun.plugin.javascript.navig;

import java.util.HashMap;

public class Link extends URL
{
  private static HashMap fieldTable = new HashMap();

  protected Link(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, null);
  }

  static
  {
    fieldTable.put("target", Boolean.TRUE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Link
 * JD-Core Version:    0.6.2
 */