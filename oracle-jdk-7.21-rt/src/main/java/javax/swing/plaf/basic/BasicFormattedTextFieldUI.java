/*    */ package javax.swing.plaf.basic;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.plaf.ComponentUI;
/*    */ 
/*    */ public class BasicFormattedTextFieldUI extends BasicTextFieldUI
/*    */ {
/*    */   public static ComponentUI createUI(JComponent paramJComponent)
/*    */   {
/* 44 */     return new BasicFormattedTextFieldUI();
/*    */   }
/*    */ 
/*    */   protected String getPropertyPrefix()
/*    */   {
/* 55 */     return "FormattedTextField";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.basic.BasicFormattedTextFieldUI
 * JD-Core Version:    0.6.2
 */