/*    */ package javax.swing.plaf.nimbus;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JProgressBar;
/*    */ 
/*    */ class ProgressBarIndeterminateState extends State
/*    */ {
/*    */   ProgressBarIndeterminateState()
/*    */   {
/* 33 */     super("Indeterminate");
/*    */   }
/*    */ 
/*    */   protected boolean isInState(JComponent paramJComponent)
/*    */   {
/* 38 */     return ((paramJComponent instanceof JProgressBar)) && (((JProgressBar)paramJComponent).isIndeterminate());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.nimbus.ProgressBarIndeterminateState
 * JD-Core Version:    0.6.2
 */