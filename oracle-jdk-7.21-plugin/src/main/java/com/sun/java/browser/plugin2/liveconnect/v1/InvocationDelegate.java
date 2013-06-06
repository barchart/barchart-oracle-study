package com.sun.java.browser.plugin2.liveconnect.v1;

public abstract interface InvocationDelegate
{
  public abstract boolean invoke(String paramString, Object paramObject, Object[] paramArrayOfObject, boolean paramBoolean1, boolean paramBoolean2, Result[] paramArrayOfResult)
    throws Exception;

  public abstract boolean getField(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, Result[] paramArrayOfResult)
    throws Exception;

  public abstract boolean setField(String paramString, Object paramObject1, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2)
    throws Exception;

  public abstract boolean hasField(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean);

  public abstract boolean hasMethod(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean);

  public abstract boolean hasFieldOrMethod(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean);

  public abstract Object findClass(String paramString);

  public abstract Object newInstance(Object paramObject, Object[] paramArrayOfObject)
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.java.browser.plugin2.liveconnect.v1.InvocationDelegate
 * JD-Core Version:    0.6.2
 */