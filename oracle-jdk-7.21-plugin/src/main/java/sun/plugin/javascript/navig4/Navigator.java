package sun.plugin.javascript.navig4;

import java.util.HashMap;

public class Navigator extends sun.plugin.javascript.navig.Navigator
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();

  protected Navigator(int paramInt)
  {
    super(paramInt);
    addObjectTable(fieldTable, methodTable);
  }

  static
  {
    methodTable.put("preference", Boolean.TRUE);
    fieldTable.put("language", Boolean.FALSE);
    fieldTable.put("platform", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.Navigator
 * JD-Core Version:    0.6.2
 */