/*    */ package javax.swing;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.FocusTraversalPolicy;
/*    */ 
/*    */ public abstract class InternalFrameFocusTraversalPolicy extends FocusTraversalPolicy
/*    */ {
/*    */   public Component getInitialComponent(JInternalFrame paramJInternalFrame)
/*    */   {
/* 66 */     return getDefaultComponent(paramJInternalFrame);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.InternalFrameFocusTraversalPolicy
 * JD-Core Version:    0.6.2
 */