/*    */ package javax.swing.tree;
/*    */ 
/*    */ import javax.swing.event.TreeExpansionEvent;
/*    */ 
/*    */ public class ExpandVetoException extends Exception
/*    */ {
/*    */   protected TreeExpansionEvent event;
/*    */ 
/*    */   public ExpandVetoException(TreeExpansionEvent paramTreeExpansionEvent)
/*    */   {
/* 50 */     this(paramTreeExpansionEvent, null);
/*    */   }
/*    */ 
/*    */   public ExpandVetoException(TreeExpansionEvent paramTreeExpansionEvent, String paramString)
/*    */   {
/* 60 */     super(paramString);
/* 61 */     this.event = paramTreeExpansionEvent;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.tree.ExpandVetoException
 * JD-Core Version:    0.6.2
 */