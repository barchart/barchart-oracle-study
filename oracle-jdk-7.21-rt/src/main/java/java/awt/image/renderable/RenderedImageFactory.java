package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;

public abstract interface RenderedImageFactory
{
  public abstract RenderedImage create(ParameterBlock paramParameterBlock, RenderingHints paramRenderingHints);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.image.renderable.RenderedImageFactory
 * JD-Core Version:    0.6.2
 */