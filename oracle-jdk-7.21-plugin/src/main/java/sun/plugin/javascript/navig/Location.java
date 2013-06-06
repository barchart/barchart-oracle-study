package sun.plugin.javascript.navig;

import java.util.HashMap;

class Location extends URL
{
  private static HashMap methodTable = new HashMap();

  Location(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(null, methodTable);
  }

  static
  {
    methodTable.put("reload", Boolean.FALSE);
    methodTable.put("replace", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Location
 * JD-Core Version:    0.6.2
 */