package sun.plugin.javascript.navig;

import java.util.HashMap;

public class Image extends JSObject
{
  private static HashMap fieldTable = new HashMap();

  protected Image(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, null);
  }

  static
  {
    fieldTable.put("border", Boolean.FALSE);
    fieldTable.put("complete", Boolean.FALSE);
    fieldTable.put("height", Boolean.FALSE);
    fieldTable.put("hspace", Boolean.FALSE);
    fieldTable.put("lowsrc", Boolean.TRUE);
    fieldTable.put("name", Boolean.FALSE);
    fieldTable.put("src", Boolean.TRUE);
    fieldTable.put("vspace", Boolean.FALSE);
    fieldTable.put("width", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Image
 * JD-Core Version:    0.6.2
 */