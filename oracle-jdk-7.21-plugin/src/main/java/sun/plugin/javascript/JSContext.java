package sun.plugin.javascript;

import netscape.javascript.JSObject;

public abstract interface JSContext
{
  public abstract JSObject getJSObject();

  public abstract JSObject getOneWayJSObject();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.JSContext
 * JD-Core Version:    0.6.2
 */