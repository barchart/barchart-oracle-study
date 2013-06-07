/*    */ package sun.security.jca;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ 
/*    */ public final class JCAUtil
/*    */ {
/* 45 */   private static final Object LOCK = JCAUtil.class;
/*    */   private static volatile SecureRandom secureRandom;
/*    */   private static final int ARRAY_SIZE = 4096;
/*    */ 
/*    */   public static int getTempArraySize(int paramInt)
/*    */   {
/* 60 */     return Math.min(4096, paramInt);
/*    */   }
/*    */ 
/*    */   public static SecureRandom getSecureRandom()
/*    */   {
/* 72 */     SecureRandom localSecureRandom = secureRandom;
/* 73 */     if (localSecureRandom == null) {
/* 74 */       synchronized (LOCK) {
/* 75 */         localSecureRandom = secureRandom;
/* 76 */         if (localSecureRandom == null) {
/* 77 */           localSecureRandom = new SecureRandom();
/* 78 */           secureRandom = localSecureRandom;
/*    */         }
/*    */       }
/*    */     }
/* 82 */     return localSecureRandom;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.jca.JCAUtil
 * JD-Core Version:    0.6.2
 */