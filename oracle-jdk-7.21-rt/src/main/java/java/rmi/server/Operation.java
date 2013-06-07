/*    */ package java.rmi.server;
/*    */ 
/*    */ @Deprecated
/*    */ public class Operation
/*    */ {
/*    */   private String operation;
/*    */ 
/*    */   @Deprecated
/*    */   public Operation(String paramString)
/*    */   {
/* 50 */     this.operation = paramString;
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public String getOperation()
/*    */   {
/* 61 */     return this.operation;
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public String toString()
/*    */   {
/* 71 */     return this.operation;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.server.Operation
 * JD-Core Version:    0.6.2
 */