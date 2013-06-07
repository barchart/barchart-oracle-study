/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.MenuShortcut;
/*     */ 
/*     */ class java_awt_MenuShortcut_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 881 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 885 */     MenuShortcut localMenuShortcut = (MenuShortcut)paramObject;
/* 886 */     return new Expression(paramObject, localMenuShortcut.getClass(), "new", new Object[] { new Integer(localMenuShortcut.getKey()), Boolean.valueOf(localMenuShortcut.usesShiftModifier()) });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_MenuShortcut_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */