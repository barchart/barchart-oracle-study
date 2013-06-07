/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Objects;
/*     */ 
/*     */ class ArrayPersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 114 */     return (paramObject2 != null) && (paramObject1.getClass() == paramObject2.getClass()) && (Array.getLength(paramObject1) == Array.getLength(paramObject2));
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */   {
/* 121 */     Class localClass = paramObject.getClass();
/* 122 */     return new Expression(paramObject, Array.class, "newInstance", new Object[] { localClass.getComponentType(), new Integer(Array.getLength(paramObject)) });
/*     */   }
/*     */ 
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 128 */     int i = Array.getLength(paramObject1);
/* 129 */     for (int j = 0; j < i; j++) {
/* 130 */       Integer localInteger = new Integer(j);
/*     */ 
/* 133 */       Expression localExpression1 = new Expression(paramObject1, "get", new Object[] { localInteger });
/* 134 */       Expression localExpression2 = new Expression(paramObject2, "get", new Object[] { localInteger });
/*     */       try {
/* 136 */         Object localObject1 = localExpression1.getValue();
/* 137 */         Object localObject2 = localExpression2.getValue();
/* 138 */         paramEncoder.writeExpression(localExpression1);
/* 139 */         if (!Objects.equals(localObject2, paramEncoder.get(localObject1)))
/*     */         {
/* 142 */           DefaultPersistenceDelegate.invokeStatement(paramObject1, "set", new Object[] { localInteger, localObject1 }, paramEncoder);
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 147 */         paramEncoder.getExceptionListener().exceptionThrown(localException);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.ArrayPersistenceDelegate
 * JD-Core Version:    0.6.2
 */