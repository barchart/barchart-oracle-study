package com.sun.xml.internal.txw2;

abstract interface ContentVisitor
{
  public abstract void onStartDocument();

  public abstract void onEndDocument();

  public abstract void onEndTag();

  public abstract void onPcdata(StringBuilder paramStringBuilder);

  public abstract void onCdata(StringBuilder paramStringBuilder);

  public abstract void onStartTag(String paramString1, String paramString2, Attribute paramAttribute, NamespaceDecl paramNamespaceDecl);

  public abstract void onComment(StringBuilder paramStringBuilder);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.txw2.ContentVisitor
 * JD-Core Version:    0.6.2
 */