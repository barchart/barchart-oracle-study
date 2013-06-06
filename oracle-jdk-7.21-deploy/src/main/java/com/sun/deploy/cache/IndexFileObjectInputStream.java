package com.sun.deploy.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class IndexFileObjectInputStream extends ObjectInputStream
{
  public IndexFileObjectInputStream(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
  }

  protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    String str = paramObjectStreamClass.getName();
    return Class.forName(str, false, ClassLoader.getSystemClassLoader());
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.IndexFileObjectInputStream
 * JD-Core Version:    0.6.2
 */