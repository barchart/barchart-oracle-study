/*     */ package javax.swing;
/*     */ 
/*     */ final class LegacyLayoutFocusTraversalPolicy extends LayoutFocusTraversalPolicy
/*     */ {
/*     */   LegacyLayoutFocusTraversalPolicy(DefaultFocusManager paramDefaultFocusManager)
/*     */   {
/* 155 */     super(new CompareTabOrderComparator(paramDefaultFocusManager));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.LegacyLayoutFocusTraversalPolicy
 * JD-Core Version:    0.6.2
 */