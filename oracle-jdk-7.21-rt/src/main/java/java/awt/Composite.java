package java.awt;

import java.awt.image.ColorModel;

public abstract interface Composite
{
  public abstract CompositeContext createContext(ColorModel paramColorModel1, ColorModel paramColorModel2, RenderingHints paramRenderingHints);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.Composite
 * JD-Core Version:    0.6.2
 */