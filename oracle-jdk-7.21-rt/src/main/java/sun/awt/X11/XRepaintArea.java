/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.peer.ComponentPeer;
/*    */ import sun.awt.RepaintArea;
/*    */ 
/*    */ class XRepaintArea extends RepaintArea
/*    */ {
/*    */   protected void updateComponent(Component paramComponent, Graphics paramGraphics)
/*    */   {
/* 55 */     if (paramComponent != null) {
/* 56 */       ComponentPeer localComponentPeer = paramComponent.getPeer();
/* 57 */       if (localComponentPeer != null) {
/* 58 */         localComponentPeer.paint(paramGraphics);
/*    */       }
/* 60 */       super.updateComponent(paramComponent, paramGraphics);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void paintComponent(Component paramComponent, Graphics paramGraphics)
/*    */   {
/* 68 */     if (paramComponent != null) {
/* 69 */       ComponentPeer localComponentPeer = paramComponent.getPeer();
/* 70 */       if (localComponentPeer != null) {
/* 71 */         localComponentPeer.paint(paramGraphics);
/*    */       }
/* 73 */       super.paintComponent(paramComponent, paramGraphics);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XRepaintArea
 * JD-Core Version:    0.6.2
 */