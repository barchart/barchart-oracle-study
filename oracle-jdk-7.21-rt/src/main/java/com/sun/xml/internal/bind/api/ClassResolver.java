package com.sun.xml.internal.bind.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract class ClassResolver
{
  @Nullable
  public abstract Class<?> resolveElementName(@NotNull String paramString1, @NotNull String paramString2)
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.api.ClassResolver
 * JD-Core Version:    0.6.2
 */