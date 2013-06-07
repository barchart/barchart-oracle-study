package com.sun.jmx.snmp.IPAcl;

abstract interface Node
{
  public abstract void jjtOpen();

  public abstract void jjtClose();

  public abstract void jjtSetParent(Node paramNode);

  public abstract Node jjtGetParent();

  public abstract void jjtAddChild(Node paramNode, int paramInt);

  public abstract Node jjtGetChild(int paramInt);

  public abstract int jjtGetNumChildren();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.IPAcl.Node
 * JD-Core Version:    0.6.2
 */