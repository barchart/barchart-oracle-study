package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public abstract interface XPointerSchema extends XMLComponent, XMLDocumentFilter
{
  public abstract void setXPointerSchemaName(String paramString);

  public abstract String getXpointerSchemaName();

  public abstract void setParent(Object paramObject);

  public abstract Object getParent();

  public abstract void setXPointerSchemaPointer(String paramString);

  public abstract String getXPointerSchemaPointer();

  public abstract boolean isSubResourceIndentified();

  public abstract void reset();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xinclude.XPointerSchema
 * JD-Core Version:    0.6.2
 */