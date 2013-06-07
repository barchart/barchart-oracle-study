package com.sun.corba.se.impl.orb;

import java.util.Properties;

public abstract interface ParserAction
{
  public abstract String getPropertyName();

  public abstract boolean isPrefix();

  public abstract String getFieldName();

  public abstract Object apply(Properties paramProperties);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orb.ParserAction
 * JD-Core Version:    0.6.2
 */