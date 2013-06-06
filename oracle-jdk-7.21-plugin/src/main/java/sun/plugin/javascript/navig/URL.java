package sun.plugin.javascript.navig;

import java.util.HashMap;

class URL extends JSObject
{
  private static HashMap fieldTable = new HashMap();

  URL(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, null);
  }

  static
  {
    fieldTable.put("hash", Boolean.TRUE);
    fieldTable.put("host", Boolean.TRUE);
    fieldTable.put("hostname", Boolean.TRUE);
    fieldTable.put("href", Boolean.TRUE);
    fieldTable.put("pathname", Boolean.TRUE);
    fieldTable.put("port", Boolean.TRUE);
    fieldTable.put("protocol", Boolean.TRUE);
    fieldTable.put("search", Boolean.TRUE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.URL
 * JD-Core Version:    0.6.2
 */