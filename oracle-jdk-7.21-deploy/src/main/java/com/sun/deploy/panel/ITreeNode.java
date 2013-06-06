package com.sun.deploy.panel;

public abstract interface ITreeNode
{
  public abstract String getDescription();

  public abstract int getChildNodeCount();

  public abstract ITreeNode getChildNode(int paramInt);

  public abstract void addChildNode(ITreeNode paramITreeNode);

  public abstract int getPropertyCount();

  public abstract IProperty getProperty(int paramInt);

  public abstract void addProperty(IProperty paramIProperty);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.ITreeNode
 * JD-Core Version:    0.6.2
 */