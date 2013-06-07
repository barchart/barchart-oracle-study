/*     */ package java.beans;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ 
/*     */ class java_util_Map_PersistenceDelegate extends DefaultPersistenceDelegate
/*     */ {
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 659 */     Map localMap1 = (Map)paramObject1;
/* 660 */     Map localMap2 = (Map)paramObject2;
/*     */     Object localObject3;
/* 663 */     if (localMap2 != null) {
/* 664 */       for (localObject3 : localMap2.keySet().toArray())
/*     */       {
/* 666 */         if (!localMap1.containsKey(localObject3)) {
/* 667 */           invokeStatement(paramObject1, "remove", new Object[] { localObject3 }, paramEncoder);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 672 */     for (??? = localMap1.keySet().iterator(); ((Iterator)???).hasNext(); ) { Object localObject2 = ((Iterator)???).next();
/* 673 */       Expression localExpression = new Expression(paramObject1, "get", new Object[] { localObject2 });
/*     */ 
/* 675 */       localObject3 = new Expression(paramObject2, "get", new Object[] { localObject2 });
/*     */       try {
/* 677 */         Object localObject4 = localExpression.getValue();
/* 678 */         Object localObject5 = ((Expression)localObject3).getValue();
/* 679 */         paramEncoder.writeExpression(localExpression);
/* 680 */         if (!Objects.equals(localObject5, paramEncoder.get(localObject4)))
/* 681 */           invokeStatement(paramObject1, "put", new Object[] { localObject2, localObject4 }, paramEncoder);
/* 682 */         else if ((localObject5 == null) && (!localMap2.containsKey(localObject2)))
/*     */         {
/* 684 */           invokeStatement(paramObject1, "put", new Object[] { localObject2, localObject4 }, paramEncoder);
/*     */         }
/*     */       }
/*     */       catch (Exception localException) {
/* 688 */         paramEncoder.getExceptionListener().exceptionThrown(localException);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_util_Map_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */