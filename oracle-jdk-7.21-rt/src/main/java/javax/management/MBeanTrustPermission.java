/*    */ package javax.management;
/*    */ 
/*    */ import java.security.BasicPermission;
/*    */ 
/*    */ public class MBeanTrustPermission extends BasicPermission
/*    */ {
/*    */   private static final long serialVersionUID = -2952178077029018140L;
/*    */ 
/*    */   public MBeanTrustPermission(String paramString)
/*    */   {
/* 62 */     this(paramString, null);
/*    */   }
/*    */ 
/*    */   public MBeanTrustPermission(String paramString1, String paramString2)
/*    */   {
/* 77 */     super(paramString1, paramString2);
/*    */ 
/* 79 */     if ((paramString2 != null) && (paramString2.length() > 0)) {
/* 80 */       throw new IllegalArgumentException("MBeanTrustPermission actions must be null: " + paramString2);
/*    */     }
/*    */ 
/* 84 */     if ((!paramString1.equals("register")) && (!paramString1.equals("*")))
/* 85 */       throw new IllegalArgumentException("MBeanTrustPermission: Unknown target name [" + paramString1 + "]");
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanTrustPermission
 * JD-Core Version:    0.6.2
 */