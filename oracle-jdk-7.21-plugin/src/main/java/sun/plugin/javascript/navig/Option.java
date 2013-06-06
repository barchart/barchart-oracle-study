package sun.plugin.javascript.navig;

import java.util.HashMap;

class Option extends JSObject
{
  private static HashMap fieldTable = new HashMap();

  Option(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, null);
  }

  static
  {
    fieldTable.put("defaultSelected", Boolean.FALSE);
    fieldTable.put("index", Boolean.FALSE);
    fieldTable.put("selected", Boolean.TRUE);
    fieldTable.put("text", Boolean.TRUE);
    fieldTable.put("value", Boolean.TRUE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Option
 * JD-Core Version:    0.6.2
 */