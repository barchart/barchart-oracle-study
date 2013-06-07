/*    */ package sun.swing;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import javax.swing.plaf.ColorUIResource;
/*    */ 
/*    */ public class PrintColorUIResource extends ColorUIResource
/*    */ {
/*    */   private Color printColor;
/*    */ 
/*    */   public PrintColorUIResource(int paramInt, Color paramColor)
/*    */   {
/* 52 */     super(paramInt);
/* 53 */     this.printColor = paramColor;
/*    */   }
/*    */ 
/*    */   public Color getPrintColor()
/*    */   {
/* 64 */     return this.printColor != null ? this.printColor : this;
/*    */   }
/*    */ 
/*    */   private Object writeReplace()
/*    */   {
/* 89 */     return new ColorUIResource(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.PrintColorUIResource
 * JD-Core Version:    0.6.2
 */