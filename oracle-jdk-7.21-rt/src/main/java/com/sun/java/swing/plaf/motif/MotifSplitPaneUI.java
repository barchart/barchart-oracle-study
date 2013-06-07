/*    */ package com.sun.java.swing.plaf.motif;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.plaf.ComponentUI;
/*    */ import javax.swing.plaf.basic.BasicSplitPaneDivider;
/*    */ import javax.swing.plaf.basic.BasicSplitPaneUI;
/*    */ 
/*    */ public class MotifSplitPaneUI extends BasicSplitPaneUI
/*    */ {
/*    */   public static ComponentUI createUI(JComponent paramJComponent)
/*    */   {
/* 56 */     return new MotifSplitPaneUI();
/*    */   }
/*    */ 
/*    */   public BasicSplitPaneDivider createDefaultDivider()
/*    */   {
/* 63 */     return new MotifSplitPaneDivider(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.motif.MotifSplitPaneUI
 * JD-Core Version:    0.6.2
 */