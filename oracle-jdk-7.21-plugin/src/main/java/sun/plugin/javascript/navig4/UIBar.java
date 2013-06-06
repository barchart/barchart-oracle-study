package sun.plugin.javascript.navig4;

import java.util.HashMap;
import sun.plugin.javascript.navig.JSObject;

class UIBar extends JSObject
{
  private static HashMap fieldTable = new HashMap();

  UIBar(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, null);
  }

  static
  {
    fieldTable.put("visible", Boolean.TRUE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.UIBar
 * JD-Core Version:    0.6.2
 */