package org.w3c.dom.html;

public abstract interface HTMLOptionElement extends HTMLElement
{
  public abstract HTMLFormElement getForm();

  public abstract boolean getDefaultSelected();

  public abstract void setDefaultSelected(boolean paramBoolean);

  public abstract String getText();

  public abstract int getIndex();

  public abstract boolean getDisabled();

  public abstract void setDisabled(boolean paramBoolean);

  public abstract String getLabel();

  public abstract void setLabel(String paramString);

  public abstract boolean getSelected();

  public abstract void setSelected(boolean paramBoolean);

  public abstract String getValue();

  public abstract void setValue(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.html.HTMLOptionElement
 * JD-Core Version:    0.6.2
 */