package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class MultipleHostsException extends JNLPException
{
  public MultipleHostsException()
  {
    super(ResourceManager.getString("launch.error.category.security"));
  }

  public String getRealMessage()
  {
    return ResourceManager.getString("launch.error.multiplehostsreferences");
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.exceptions.MultipleHostsException
 * JD-Core Version:    0.6.2
 */