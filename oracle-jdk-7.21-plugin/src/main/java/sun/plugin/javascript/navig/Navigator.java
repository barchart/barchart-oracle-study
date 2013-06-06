package sun.plugin.javascript.navig;

import java.util.HashMap;

public class Navigator extends JSObject
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();

  protected Navigator(int paramInt)
  {
    super(paramInt, "navigator");
    addObjectTable(fieldTable, methodTable);
  }

  static
  {
    methodTable.put("javaEnabled", Boolean.TRUE);
    methodTable.put("taintEnabled", Boolean.TRUE);
    fieldTable.put("appCodeName", Boolean.FALSE);
    fieldTable.put("appName", Boolean.FALSE);
    fieldTable.put("appVersion", Boolean.FALSE);
    fieldTable.put("userAgent", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Navigator
 * JD-Core Version:    0.6.2
 */