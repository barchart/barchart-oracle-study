/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ class StaticFieldsPersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected void installFields(Encoder paramEncoder, Class<?> paramClass)
/*     */   {
/* 848 */     Field[] arrayOfField = paramClass.getFields();
/* 849 */     for (int i = 0; i < arrayOfField.length; i++) {
/* 850 */       Field localField = arrayOfField[i];
/*     */ 
/* 853 */       if (Object.class.isAssignableFrom(localField.getType()))
/* 854 */         paramEncoder.writeExpression(new Expression(localField, "get", new Object[] { null }));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */   {
/* 860 */     throw new RuntimeException("Unrecognized instance: " + paramObject);
/*     */   }
/*     */ 
/*     */   public void writeObject(Object paramObject, Encoder paramEncoder) {
/* 864 */     if (paramEncoder.getAttribute(this) == null) {
/* 865 */       paramEncoder.setAttribute(this, Boolean.TRUE);
/* 866 */       installFields(paramEncoder, paramObject.getClass());
/*     */     }
/* 868 */     super.writeObject(paramObject, paramEncoder);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.StaticFieldsPersistenceDelegate
 * JD-Core Version:    0.6.2
 */