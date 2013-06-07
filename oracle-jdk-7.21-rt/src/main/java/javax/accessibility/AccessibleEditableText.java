package javax.accessibility;

import javax.swing.text.AttributeSet;

public abstract interface AccessibleEditableText extends AccessibleText
{
  public abstract void setTextContents(String paramString);

  public abstract void insertTextAtIndex(int paramInt, String paramString);

  public abstract String getTextRange(int paramInt1, int paramInt2);

  public abstract void delete(int paramInt1, int paramInt2);

  public abstract void cut(int paramInt1, int paramInt2);

  public abstract void paste(int paramInt);

  public abstract void replaceText(int paramInt1, int paramInt2, String paramString);

  public abstract void selectText(int paramInt1, int paramInt2);

  public abstract void setAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.accessibility.AccessibleEditableText
 * JD-Core Version:    0.6.2
 */