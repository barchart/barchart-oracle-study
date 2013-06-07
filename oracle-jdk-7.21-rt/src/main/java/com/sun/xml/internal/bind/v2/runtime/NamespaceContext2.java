package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.NamespaceContext;

public abstract interface NamespaceContext2 extends NamespaceContext
{
  public abstract String declareNamespace(String paramString1, String paramString2, boolean paramBoolean);

  public abstract int force(@NotNull String paramString1, @NotNull String paramString2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.NamespaceContext2
 * JD-Core Version:    0.6.2
 */