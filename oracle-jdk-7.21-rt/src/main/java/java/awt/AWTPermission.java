/*     */ package java.awt;
/*     */ 
/*     */ import java.security.BasicPermission;
/*     */ 
/*     */ public final class AWTPermission extends BasicPermission
/*     */ {
/*     */   private static final long serialVersionUID = 8890392402588814465L;
/*     */ 
/*     */   public AWTPermission(String paramString)
/*     */   {
/* 218 */     super(paramString);
/*     */   }
/*     */ 
/*     */   public AWTPermission(String paramString1, String paramString2)
/*     */   {
/* 235 */     super(paramString1, paramString2);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.AWTPermission
 * JD-Core Version:    0.6.2
 */