package sun.plugin.dom;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.w3c.dom.DOMException;
import sun.plugin.dom.exception.BrowserNotSupportedException;

public class DOMObject
{
  private JSObject jsobj;

  public DOMObject(JSObject paramJSObject)
  {
    this.jsobj = paramJSObject;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof DOMObject))
      return false;
    return this.jsobj.equals(((DOMObject)paramObject).jsobj);
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws DOMException
  {
    try
    {
      Object[] arrayOfObject = null;
      if (paramArrayOfObject != null)
      {
        arrayOfObject = new Object[paramArrayOfObject.length];
        for (int i = 0; i < paramArrayOfObject.length; i++)
          arrayOfObject[i] = unwrapObject(paramArrayOfObject[i]);
      }
      Object localObject = this.jsobj.call(paramString, arrayOfObject);
      return wrapObject(localObject);
    }
    catch (JSException localJSException)
    {
      throw new BrowserNotSupportedException(localJSException.toString());
    }
  }

  public Object getMember(String paramString)
    throws DOMException
  {
    try
    {
      Object localObject = this.jsobj.getMember(paramString);
      return wrapObject(localObject);
    }
    catch (JSException localJSException)
    {
      throw new BrowserNotSupportedException(localJSException.toString());
    }
  }

  public void setMember(String paramString, Object paramObject)
    throws DOMException
  {
    paramObject = unwrapObject(paramObject);
    try
    {
      this.jsobj.setMember(paramString, paramObject);
    }
    catch (JSException localJSException)
    {
      throw new BrowserNotSupportedException(localJSException.toString());
    }
  }

  public void removeMember(String paramString)
    throws DOMException
  {
    try
    {
      this.jsobj.removeMember(paramString);
    }
    catch (JSException localJSException)
    {
      throw new BrowserNotSupportedException(localJSException.toString());
    }
  }

  public Object getSlot(int paramInt)
    throws DOMException
  {
    try
    {
      Object localObject = this.jsobj.getSlot(paramInt);
      return wrapObject(localObject);
    }
    catch (JSException localJSException)
    {
      throw new BrowserNotSupportedException(localJSException.toString());
    }
  }

  public void setSlot(int paramInt, Object paramObject)
    throws DOMException
  {
    paramObject = unwrapObject(paramObject);
    try
    {
      this.jsobj.setSlot(paramInt, paramObject);
    }
    catch (JSException localJSException)
    {
      throw new BrowserNotSupportedException(localJSException.toString());
    }
  }

  public String toString()
  {
    Object localObject = getJSObject();
    if (localObject == null)
      return null;
    return localObject.toString();
  }

  private Object wrapObject(Object paramObject)
  {
    if (paramObject == null)
      return paramObject;
    if ((paramObject instanceof JSObject))
      return new DOMObject((JSObject)paramObject);
    return paramObject;
  }

  private Object unwrapObject(Object paramObject)
  {
    if (paramObject == null)
      return paramObject;
    if ((paramObject instanceof DOMObject))
    {
      DOMObject localDOMObject = (DOMObject)paramObject;
      return localDOMObject.getJSObject();
    }
    return paramObject;
  }

  public Object getJSObject()
  {
    return this.jsobj;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMObject
 * JD-Core Version:    0.6.2
 */