package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLable;

abstract interface ResourceType extends XMLable
{
  public abstract void visit(ResourceVisitor paramResourceVisitor);
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.jnl.ResourceType
 * JD-Core Version:    0.6.2
 */