package sun.plugin.javascript.navig;

import java.util.HashMap;

class History extends JSObject
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();

  History(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, methodTable);
  }

  static
  {
    methodTable.put("back", Boolean.FALSE);
    methodTable.put("forward", Boolean.FALSE);
    methodTable.put("go", Boolean.FALSE);
    methodTable.put("toString", Boolean.TRUE);
    fieldTable.put("current", Boolean.FALSE);
    fieldTable.put("length", Boolean.FALSE);
    fieldTable.put("next", Boolean.FALSE);
    fieldTable.put("previous", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.History
 * JD-Core Version:    0.6.2
 */