/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.Menu;
/*     */ 
/*     */ class java_awt_Menu_PersistenceDelegate extends DefaultPersistenceDelegate
/*     */ {
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 988 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 989 */     Menu localMenu1 = (Menu)paramObject1;
/* 990 */     Menu localMenu2 = (Menu)paramObject2;
/* 991 */     for (int i = localMenu2.getItemCount(); i < localMenu1.getItemCount(); i++)
/* 992 */       invokeStatement(paramObject1, "add", new Object[] { localMenu1.getItem(i) }, paramEncoder);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_Menu_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */