/*    */ package sun.nio.ch;
/*    */ 
/*    */ import com.sun.nio.sctp.Association;
/*    */ import com.sun.nio.sctp.ShutdownNotification;
/*    */ 
/*    */ public class SctpShutdown extends ShutdownNotification
/*    */   implements SctpNotification
/*    */ {
/*    */   private Association association;
/*    */   private int assocId;
/*    */ 
/*    */   private SctpShutdown(int paramInt)
/*    */   {
/* 43 */     this.assocId = paramInt;
/*    */   }
/*    */ 
/*    */   public int assocId()
/*    */   {
/* 48 */     return this.assocId;
/*    */   }
/*    */ 
/*    */   public void setAssociation(Association paramAssociation)
/*    */   {
/* 53 */     this.association = paramAssociation;
/*    */   }
/*    */ 
/*    */   public Association association()
/*    */   {
/* 58 */     assert (this.association != null);
/* 59 */     return this.association;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 64 */     StringBuilder localStringBuilder = new StringBuilder();
/* 65 */     localStringBuilder.append(super.toString()).append(" [");
/* 66 */     localStringBuilder.append("Association:").append(this.association).append("]");
/* 67 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpShutdown
 * JD-Core Version:    0.6.2
 */