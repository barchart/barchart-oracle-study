package netscape.javascript;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import java.applet.Applet;
import sun.plugin.javascript.JSContext;
import sun.plugin2.applet2.Plugin2Context;

public abstract class JSObject
{
  public abstract Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException;

  public abstract Object eval(String paramString)
    throws JSException;

  public abstract Object getMember(String paramString)
    throws JSException;

  public abstract void setMember(String paramString, Object paramObject)
    throws JSException;

  public abstract void removeMember(String paramString)
    throws JSException;

  public abstract Object getSlot(int paramInt)
    throws JSException;

  public abstract void setSlot(int paramInt, Object paramObject)
    throws JSException;

  public static JSObject getWindow(Applet paramApplet)
    throws JSException
  {
    try
    {
      Object localObject1;
      Object localObject2;
      Object localObject3;
      JSContext localJSContext;
      if (paramApplet != null)
      {
        localObject1 = paramApplet.getParameter("MAYSCRIPT");
        localObject2 = paramApplet.getAppletContext();
        localObject3 = null;
        if ((localObject2 instanceof JSContext))
        {
          localJSContext = (JSContext)localObject2;
          localObject3 = localJSContext.getJSObject();
        }
        if (localObject3 != null)
          return localObject3;
      }
      else
      {
        localObject1 = ToolkitStore.get().getAppContext();
        if (localObject1 != null)
        {
          localObject2 = (Plugin2Context)((AppContext)localObject1).get("Plugin2CtxKey");
          if (localObject2 != null)
          {
            localObject3 = ((Plugin2Context)localObject2).getHost();
            if ((localObject3 != null) && ((localObject3 instanceof JSContext)))
            {
              localJSContext = (JSContext)localObject3;
              JSObject localJSObject = localJSContext.getOneWayJSObject();
              if (localJSObject != null)
                return localJSObject;
            }
          }
        }
      }
    }
    catch (Throwable localThrowable)
    {
      throw ((JSException)new JSException(6, localThrowable).initCause(localThrowable));
    }
    throw new JSException();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.javascript.JSObject
 * JD-Core Version:    0.6.2
 */