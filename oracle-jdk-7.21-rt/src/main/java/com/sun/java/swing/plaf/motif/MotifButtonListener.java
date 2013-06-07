/*    */ package com.sun.java.swing.plaf.motif;
/*    */ 
/*    */ import javax.swing.AbstractButton;
/*    */ import javax.swing.plaf.basic.BasicButtonListener;
/*    */ 
/*    */ public class MotifButtonListener extends BasicButtonListener
/*    */ {
/*    */   public MotifButtonListener(AbstractButton paramAbstractButton)
/*    */   {
/* 43 */     super(paramAbstractButton);
/*    */   }
/*    */ 
/*    */   protected void checkOpacity(AbstractButton paramAbstractButton) {
/* 47 */     paramAbstractButton.setOpaque(false);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.motif.MotifButtonListener
 * JD-Core Version:    0.6.2
 */