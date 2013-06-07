/*     */ package javax.swing;
/*     */ 
/*     */ public abstract class InputVerifier
/*     */ {
/*     */   public abstract boolean verify(JComponent paramJComponent);
/*     */ 
/*     */   public boolean shouldYieldFocus(JComponent paramJComponent)
/*     */   {
/* 132 */     return verify(paramJComponent);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.InputVerifier
 * JD-Core Version:    0.6.2
 */