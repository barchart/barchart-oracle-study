/*    */ package sun.security.krb5.internal;
/*    */ 
/*    */ import sun.security.krb5.KrbException;
/*    */ 
/*    */ public class KrbErrException extends KrbException
/*    */ {
/*    */   private static final long serialVersionUID = 2186533836785448317L;
/*    */ 
/*    */   public KrbErrException(int paramInt)
/*    */   {
/* 38 */     super(paramInt);
/*    */   }
/*    */ 
/*    */   public KrbErrException(int paramInt, String paramString) {
/* 42 */     super(paramInt, paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.KrbErrException
 * JD-Core Version:    0.6.2
 */