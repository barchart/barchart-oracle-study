/*    */ package sun.nio.ch;
/*    */ 
/*    */ import com.sun.nio.sctp.Association;
/*    */ 
/*    */ public class SctpAssociationImpl extends Association
/*    */ {
/*    */   public SctpAssociationImpl(int paramInt1, int paramInt2, int paramInt3)
/*    */   {
/* 36 */     super(paramInt1, paramInt2, paramInt3);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 41 */     StringBuffer localStringBuffer = new StringBuffer(super.toString());
/* 42 */     return "[associationID:" + associationID() + ", maxIn:" + maxInboundStreams() + ", maxOut:" + maxOutboundStreams() + "]";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpAssociationImpl
 * JD-Core Version:    0.6.2
 */