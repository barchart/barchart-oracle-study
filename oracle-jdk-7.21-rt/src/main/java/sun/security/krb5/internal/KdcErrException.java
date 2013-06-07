/*    */ package sun.security.krb5.internal;
/*    */ 
/*    */ import sun.security.krb5.KrbException;
/*    */ 
/*    */ public class KdcErrException extends KrbException
/*    */ {
/*    */   private static final long serialVersionUID = -8788186031117310306L;
/*    */ 
/*    */   public KdcErrException(int paramInt)
/*    */   {
/* 39 */     super(paramInt);
/*    */   }
/*    */ 
/*    */   public KdcErrException(int paramInt, String paramString) {
/* 43 */     super(paramInt, paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.KdcErrException
 * JD-Core Version:    0.6.2
 */