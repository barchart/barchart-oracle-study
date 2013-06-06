package com.sun.deploy.uitoolkit.ui;

import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.main.client.ModalityInterface;

public abstract interface ModalityHelper
{
  public abstract void reactivateDialog(AbstractDialog paramAbstractDialog);

  public abstract boolean installModalityListener(ModalityInterface paramModalityInterface);

  public abstract void pushManagerShowingSystemDialog();

  public abstract Plugin2Manager getManagerShowingSystemDialog();

  public abstract void popManagerShowingSystemDialog();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.ui.ModalityHelper
 * JD-Core Version:    0.6.2
 */