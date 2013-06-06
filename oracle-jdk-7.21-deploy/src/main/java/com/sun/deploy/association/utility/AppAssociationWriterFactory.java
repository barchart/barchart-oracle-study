package com.sun.deploy.association.utility;

import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.xdg.XDGAppAssociationWriter;

public class AppAssociationWriterFactory
{
  public static AppAssociationWriter newInstance(LocalApplicationProperties paramLocalApplicationProperties)
  {
    if (GnomeAssociationUtil.isAssociationSupported())
      return new GnomeAppAssociationWriter(paramLocalApplicationProperties);
    return new XDGAppAssociationWriter(paramLocalApplicationProperties);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.association.utility.AppAssociationWriterFactory
 * JD-Core Version:    0.6.2
 */