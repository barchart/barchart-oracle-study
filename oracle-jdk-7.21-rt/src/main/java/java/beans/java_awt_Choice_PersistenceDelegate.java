/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.Choice;
/*     */ 
/*     */ class java_awt_Choice_PersistenceDelegate extends DefaultPersistenceDelegate
/*     */ {
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 976 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 977 */     Choice localChoice1 = (Choice)paramObject1;
/* 978 */     Choice localChoice2 = (Choice)paramObject2;
/* 979 */     for (int i = localChoice2.getItemCount(); i < localChoice1.getItemCount(); i++)
/* 980 */       invokeStatement(paramObject1, "add", new Object[] { localChoice1.getItem(i) }, paramEncoder);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_Choice_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */