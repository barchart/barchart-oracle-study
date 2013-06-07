package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;

public abstract interface XPathFactory
{
  public abstract XPath create(String paramString, SourceLocator paramSourceLocator, PrefixResolver paramPrefixResolver, int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.XPathFactory
 * JD-Core Version:    0.6.2
 */