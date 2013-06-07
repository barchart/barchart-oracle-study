/*    */ package javax.swing.plaf.nimbus;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JInternalFrame;
/*    */ 
/*    */ class InternalFrameTitlePaneWindowFocusedState extends State
/*    */ {
/*    */   InternalFrameTitlePaneWindowFocusedState()
/*    */   {
/* 33 */     super("WindowFocused");
/*    */   }
/*    */ 
/*    */   protected boolean isInState(JComponent paramJComponent)
/*    */   {
/* 38 */     return ((paramJComponent instanceof JInternalFrame)) && (((JInternalFrame)paramJComponent).isSelected());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.nimbus.InternalFrameTitlePaneWindowFocusedState
 * JD-Core Version:    0.6.2
 */