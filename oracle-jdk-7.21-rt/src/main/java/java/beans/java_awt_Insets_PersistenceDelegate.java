/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.Insets;
/*     */ 
/*     */ final class java_awt_Insets_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 714 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 718 */     Insets localInsets = (Insets)paramObject;
/* 719 */     Object[] arrayOfObject = { Integer.valueOf(localInsets.top), Integer.valueOf(localInsets.left), Integer.valueOf(localInsets.bottom), Integer.valueOf(localInsets.right) };
/*     */ 
/* 725 */     return new Expression(localInsets, localInsets.getClass(), "new", arrayOfObject);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_Insets_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */