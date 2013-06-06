package sun.plugin.com;

import java.awt.Font;

public abstract interface AmbientProperty
{
  public abstract void setBackground(int paramInt1, int paramInt2, int paramInt3);

  public abstract void setForeground(int paramInt1, int paramInt2, int paramInt3);

  public abstract void setFont(String paramString, int paramInt1, int paramInt2);

  public abstract int getBackground();

  public abstract int getForeground();

  public abstract Font getFont();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.com.AmbientProperty
 * JD-Core Version:    0.6.2
 */