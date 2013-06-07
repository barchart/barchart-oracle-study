package com.sun.xml.internal.ws.org.objectweb.asm;

class Edge
{
  static final int NORMAL = 0;
  static final int EXCEPTION = 2147483647;
  int info;
  Label successor;
  Edge next;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.org.objectweb.asm.Edge
 * JD-Core Version:    0.6.2
 */