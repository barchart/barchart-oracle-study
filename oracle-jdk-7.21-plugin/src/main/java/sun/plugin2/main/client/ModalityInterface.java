package sun.plugin2.main.client;

import com.sun.deploy.uitoolkit.ui.AbstractDialog;

public abstract interface ModalityInterface
{
  public abstract void modalityPushed(AbstractDialog paramAbstractDialog);

  public abstract void modalityPopped(AbstractDialog paramAbstractDialog);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.ModalityInterface
 * JD-Core Version:    0.6.2
 */