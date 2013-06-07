/*    */ package sun.security.krb5.internal;
/*    */ 
/*    */ import sun.security.krb5.KrbException;
/*    */ 
/*    */ public class KrbApErrException extends KrbException
/*    */ {
/*    */   private static final long serialVersionUID = 7545264413323118315L;
/*    */ 
/*    */   public KrbApErrException(int paramInt)
/*    */   {
/* 39 */     super(paramInt);
/*    */   }
/*    */ 
/*    */   public KrbApErrException(int paramInt, String paramString) {
/* 43 */     super(paramInt, paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.KrbApErrException
 * JD-Core Version:    0.6.2
 */