/*    */ package javax.swing.event;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ import javax.swing.undo.UndoableEdit;
/*    */ 
/*    */ public class UndoableEditEvent extends EventObject
/*    */ {
/*    */   private UndoableEdit myEdit;
/*    */ 
/*    */   public UndoableEditEvent(Object paramObject, UndoableEdit paramUndoableEdit)
/*    */   {
/* 55 */     super(paramObject);
/* 56 */     this.myEdit = paramUndoableEdit;
/*    */   }
/*    */ 
/*    */   public UndoableEdit getEdit()
/*    */   {
/* 65 */     return this.myEdit;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.event.UndoableEditEvent
 * JD-Core Version:    0.6.2
 */