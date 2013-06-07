package com.sun.java.swing.plaf.windows;

import javax.swing.JMenuItem;

abstract interface WindowsMenuItemUIAccessor
{
  public abstract JMenuItem getMenuItem();

  public abstract TMSchema.State getState(JMenuItem paramJMenuItem);

  public abstract TMSchema.Part getPart(JMenuItem paramJMenuItem);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsMenuItemUIAccessor
 * JD-Core Version:    0.6.2
 */