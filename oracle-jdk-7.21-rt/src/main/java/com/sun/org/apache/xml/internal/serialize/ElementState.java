package com.sun.org.apache.xml.internal.serialize;

import java.util.Hashtable;

public class ElementState
{
  public String rawName;
  public String localName;
  public String namespaceURI;
  public boolean preserveSpace;
  public boolean empty;
  public boolean afterElement;
  public boolean afterComment;
  public boolean doCData;
  public boolean unescaped;
  public boolean inCData;
  public Hashtable prefixes;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.serialize.ElementState
 * JD-Core Version:    0.6.2
 */