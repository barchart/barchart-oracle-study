package com.sun.org.apache.xalan.internal.xsltc;

public abstract interface DOMEnhancedForDTM extends DOM
{
  public abstract short[] getMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt);

  public abstract int[] getReverseMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt);

  public abstract short[] getNamespaceMapping(String[] paramArrayOfString);

  public abstract short[] getReverseNamespaceMapping(String[] paramArrayOfString);

  public abstract String getDocumentURI();

  public abstract void setDocumentURI(String paramString);

  public abstract int getExpandedTypeID2(int paramInt);

  public abstract boolean hasDOMSource();

  public abstract int getElementById(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM
 * JD-Core Version:    0.6.2
 */