/*    */ package javax.swing.plaf.nimbus;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JSplitPane;
/*    */ 
/*    */ class SplitPaneVerticalState extends State
/*    */ {
/*    */   SplitPaneVerticalState()
/*    */   {
/* 33 */     super("Vertical");
/*    */   }
/*    */ 
/*    */   protected boolean isInState(JComponent paramJComponent)
/*    */   {
/* 38 */     return ((paramJComponent instanceof JSplitPane)) && (((JSplitPane)paramJComponent).getOrientation() == 1);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.nimbus.SplitPaneVerticalState
 * JD-Core Version:    0.6.2
 */