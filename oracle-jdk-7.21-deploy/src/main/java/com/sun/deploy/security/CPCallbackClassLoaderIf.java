package com.sun.deploy.security;

import java.security.CodeSource;

public abstract interface CPCallbackClassLoaderIf
{
  public abstract CodeSource[] getTrustedCodeSources(CodeSource[] paramArrayOfCodeSource);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.CPCallbackClassLoaderIf
 * JD-Core Version:    0.6.2
 */