/*    */ package javax.sql;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class RowSetEvent extends EventObject
/*    */ {
/*    */   static final long serialVersionUID = -1875450876546332005L;
/*    */ 
/*    */   public RowSetEvent(RowSet paramRowSet)
/*    */   {
/* 55 */     super(paramRowSet);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.RowSetEvent
 * JD-Core Version:    0.6.2
 */