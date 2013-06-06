package com.sun.deploy.association.utility;

import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.xdg.XDGAppAssociationReader;

public class AppAssociationReaderFactory
{
  public static AppAssociationReader newInstance(LocalApplicationProperties paramLocalApplicationProperties)
  {
    if (GnomeAssociationUtil.isAssociationSupported())
      return new GnomeAppAssociationReader(paramLocalApplicationProperties);
    return new XDGAppAssociationReader(paramLocalApplicationProperties);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.association.utility.AppAssociationReaderFactory
 * JD-Core Version:    0.6.2
 */