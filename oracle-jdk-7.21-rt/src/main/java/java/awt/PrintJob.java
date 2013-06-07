/*    */ package java.awt;
/*    */ 
/*    */ public abstract class PrintJob
/*    */ {
/*    */   public abstract Graphics getGraphics();
/*    */ 
/*    */   public abstract Dimension getPageDimension();
/*    */ 
/*    */   public abstract int getPageResolution();
/*    */ 
/*    */   public abstract boolean lastPageFirst();
/*    */ 
/*    */   public abstract void end();
/*    */ 
/*    */   public void finalize()
/*    */   {
/* 77 */     end();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.PrintJob
 * JD-Core Version:    0.6.2
 */