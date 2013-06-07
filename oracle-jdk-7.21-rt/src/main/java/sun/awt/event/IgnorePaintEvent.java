/*    */ package sun.awt.event;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.event.PaintEvent;
/*    */ 
/*    */ public class IgnorePaintEvent extends PaintEvent
/*    */ {
/*    */   public IgnorePaintEvent(Component paramComponent, int paramInt, Rectangle paramRectangle)
/*    */   {
/* 40 */     super(paramComponent, paramInt, paramRectangle);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.event.IgnorePaintEvent
 * JD-Core Version:    0.6.2
 */