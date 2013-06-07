/*    */ package sun.awt.image;
/*    */ 
/*    */ import java.awt.GraphicsConfiguration;
/*    */ import java.awt.GraphicsDevice;
/*    */ 
/*    */ public class BufferedImageDevice extends GraphicsDevice
/*    */ {
/*    */   GraphicsConfiguration gc;
/*    */ 
/*    */   public BufferedImageDevice(BufferedImageGraphicsConfig paramBufferedImageGraphicsConfig)
/*    */   {
/* 36 */     this.gc = paramBufferedImageGraphicsConfig;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 48 */     return 2;
/*    */   }
/*    */ 
/*    */   public String getIDstring()
/*    */   {
/* 58 */     return "BufferedImage";
/*    */   }
/*    */ 
/*    */   public GraphicsConfiguration[] getConfigurations()
/*    */   {
/* 69 */     return new GraphicsConfiguration[] { this.gc };
/*    */   }
/*    */ 
/*    */   public GraphicsConfiguration getDefaultConfiguration()
/*    */   {
/* 79 */     return this.gc;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.image.BufferedImageDevice
 * JD-Core Version:    0.6.2
 */