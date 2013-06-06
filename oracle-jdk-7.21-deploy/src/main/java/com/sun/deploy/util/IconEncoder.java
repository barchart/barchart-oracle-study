package com.sun.deploy.util;

import java.io.File;

public abstract interface IconEncoder
{
  public abstract void convert(File[] paramArrayOfFile, int[] paramArrayOfInt, int paramInt, String paramString);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.IconEncoder
 * JD-Core Version:    0.6.2
 */