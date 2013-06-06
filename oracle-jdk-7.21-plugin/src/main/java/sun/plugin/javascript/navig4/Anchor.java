package sun.plugin.javascript.navig4;

import java.util.HashMap;

public class Anchor extends sun.plugin.javascript.navig.Anchor
{
  private static HashMap fieldTable = new HashMap();

  protected Anchor(int paramInt, String paramString)
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
 * Qualified Name:     sun.plugin.javascript.navig4.Anchor
 * JD-Core Version:    0.6.2
 */