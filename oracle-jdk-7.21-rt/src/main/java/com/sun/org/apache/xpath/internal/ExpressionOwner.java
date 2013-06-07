package com.sun.org.apache.xpath.internal;

public abstract interface ExpressionOwner
{
  public abstract Expression getExpression();

  public abstract void setExpression(Expression paramExpression);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.ExpressionOwner
 * JD-Core Version:    0.6.2
 */