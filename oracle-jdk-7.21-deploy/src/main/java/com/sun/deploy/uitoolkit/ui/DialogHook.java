package com.sun.deploy.uitoolkit.ui;

public abstract interface DialogHook
{
  public abstract Object beforeDialog(Object paramObject);

  public abstract void afterDialog();

  public abstract boolean ignoreOwnerVisibility();
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.ui.DialogHook
 * JD-Core Version:    0.6.2
 */