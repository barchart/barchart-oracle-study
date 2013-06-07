/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ 
/*      */ class StatusOKButtonListener
/*      */   implements ActionListener
/*      */ {
/*      */   private ToolDialog sd;
/*      */ 
/*      */   StatusOKButtonListener(ToolDialog paramToolDialog)
/*      */   {
/* 3490 */     this.sd = paramToolDialog;
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 3494 */     this.sd.setVisible(false);
/* 3495 */     this.sd.dispose();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.StatusOKButtonListener
 * JD-Core Version:    0.6.2
 */