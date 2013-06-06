package sun.plugin.javascript.navig;

import com.sun.deploy.trace.Trace;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import netscape.javascript.JSException;

public class JSObject extends sun.plugin.javascript.JSObject
{
  protected String context;
  protected int instance;
  private LinkedList methodVector = new LinkedList();
  private LinkedList fieldVector = new LinkedList();
  private static JSObjectResolver resolver = null;

  protected JSObject(int paramInt, String paramString)
  {
    this.instance = paramInt;
    this.context = paramString;
  }

  public Object getMember(String paramString)
    throws JSException
  {
    try
    {
      int i = 0;
      synchronized (this.fieldVector)
      {
        Iterator localIterator = this.fieldVector.iterator();
        while (localIterator.hasNext())
        {
          HashMap localHashMap = (HashMap)localIterator.next();
          Boolean localBoolean = (Boolean)localHashMap.get(paramString);
          if (localBoolean != null)
          {
            i = 1;
            break;
          }
        }
      }
      if (i != 0)
        return evaluate(this.context + "." + paramString);
    }
    catch (Throwable localThrowable)
    {
    }
    return super.getMember(paramString);
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    try
    {
      if (paramObject == null)
        paramObject = "";
      int i = 0;
      synchronized (this.fieldVector)
      {
        Iterator localIterator = this.fieldVector.iterator();
        while (localIterator.hasNext())
        {
          HashMap localHashMap = (HashMap)localIterator.next();
          Boolean localBoolean = (Boolean)localHashMap.get(paramString);
          if ((localBoolean != null) && (localBoolean.booleanValue()))
          {
            i = 1;
            break;
          }
        }
      }
      if (i != 0)
      {
        if ((paramObject instanceof String))
          evaluate(this.context + "." + paramString + "='" + paramObject.toString() + "'");
        else
          evaluate(this.context + "." + paramString + "=" + paramObject.toString());
        return;
      }
    }
    catch (Throwable localThrowable)
    {
    }
    super.setMember(paramString, paramObject);
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    try
    {
      int i = 0;
      synchronized (this.methodVector)
      {
        Iterator localIterator = this.methodVector.iterator();
        while (localIterator.hasNext())
        {
          HashMap localHashMap = (HashMap)localIterator.next();
          Boolean localBoolean = (Boolean)localHashMap.get(paramString);
          if (localBoolean != null)
          {
            i = 1;
            break;
          }
        }
      }
      if (i != 0)
      {
        ??? = this.context + "." + paramString + "(";
        if (paramArrayOfObject != null)
          for (int j = 0; j < paramArrayOfObject.length; j++)
          {
            if ((paramArrayOfObject[j] instanceof String))
              ??? = (String)??? + "'" + paramArrayOfObject[j].toString() + "'";
            else
              ??? = (String)??? + paramArrayOfObject[j].toString();
            if (j != paramArrayOfObject.length - 1)
              ??? = (String)??? + ", ";
          }
        ??? = (String)??? + ")";
        return evaluate((String)???);
      }
    }
    catch (Throwable localThrowable)
    {
    }
    return super.call(paramString, paramArrayOfObject);
  }

  public Object eval(String paramString)
    throws JSException
  {
    return evaluate(paramString);
  }

  public String toString()
  {
    return this.context;
  }

  protected synchronized Object evaluate(String paramString)
    throws JSException
  {
    String str = evalScript(this.instance, "javascript: " + paramString);
    Trace.msgLiveConnectPrintln("jsobject.eval", new Object[] { paramString, str });
    return str;
  }

  public native String evalScript(int paramInt, String paramString);

  protected static JSObjectResolver getResolver()
  {
    if (resolver == null)
      resolver = new JSObjectFactory();
    return resolver;
  }

  protected static void setResolver(JSObjectResolver paramJSObjectResolver)
    throws JSException
  {
    if (resolver == null)
      resolver = paramJSObjectResolver;
    else
      throw new JSException("JSObject resolver already exists.");
  }

  protected Object resolveObject(String paramString1, String paramString2, Object paramObject)
    throws JSException
  {
    JSObjectResolver localJSObjectResolver = getResolver();
    return localJSObjectResolver.resolveObject(this, paramString1, this.instance, paramString2, paramObject);
  }

  protected Object resolveObject(String paramString1, String paramString2)
    throws JSException
  {
    return resolveObject(paramString1, paramString2, null);
  }

  protected final void addObjectTable(HashMap paramHashMap1, HashMap paramHashMap2)
  {
    if (paramHashMap1 != null)
      synchronized (this.fieldVector)
      {
        this.fieldVector.add(0, paramHashMap1);
      }
    if (paramHashMap2 != null)
      synchronized (this.methodVector)
      {
        this.methodVector.add(0, paramHashMap2);
      }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.JSObject
 * JD-Core Version:    0.6.2
 */