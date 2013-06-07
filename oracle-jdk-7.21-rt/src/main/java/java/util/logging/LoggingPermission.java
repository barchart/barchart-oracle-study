/*    */ package java.util.logging;
/*    */ 
/*    */ import java.security.BasicPermission;
/*    */ 
/*    */ public final class LoggingPermission extends BasicPermission
/*    */ {
/*    */   private static final long serialVersionUID = 63564341580231582L;
/*    */ 
/*    */   public LoggingPermission(String paramString1, String paramString2)
/*    */     throws IllegalArgumentException
/*    */   {
/* 70 */     super(paramString1);
/* 71 */     if (!paramString1.equals("control")) {
/* 72 */       throw new IllegalArgumentException("name: " + paramString1);
/*    */     }
/* 74 */     if ((paramString2 != null) && (paramString2.length() > 0))
/* 75 */       throw new IllegalArgumentException("actions: " + paramString2);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.logging.LoggingPermission
 * JD-Core Version:    0.6.2
 */