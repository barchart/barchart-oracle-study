/*     */ package java.beans;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ 
/*     */ class java_util_List_PersistenceDelegate extends DefaultPersistenceDelegate
/*     */ {
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 623 */     List localList1 = (List)paramObject1;
/* 624 */     List localList2 = (List)paramObject2;
/* 625 */     int i = localList1.size();
/* 626 */     int j = localList2 == null ? 0 : localList2.size();
/* 627 */     if (i < j) {
/* 628 */       invokeStatement(paramObject1, "clear", new Object[0], paramEncoder);
/* 629 */       j = 0;
/*     */     }
/* 631 */     for (int k = 0; k < j; k++) {
/* 632 */       Integer localInteger = new Integer(k);
/*     */ 
/* 634 */       Expression localExpression1 = new Expression(paramObject1, "get", new Object[] { localInteger });
/* 635 */       Expression localExpression2 = new Expression(paramObject2, "get", new Object[] { localInteger });
/*     */       try {
/* 637 */         Object localObject1 = localExpression1.getValue();
/* 638 */         Object localObject2 = localExpression2.getValue();
/* 639 */         paramEncoder.writeExpression(localExpression1);
/* 640 */         if (!Objects.equals(localObject2, paramEncoder.get(localObject1)))
/* 641 */           invokeStatement(paramObject1, "set", new Object[] { localInteger, localObject1 }, paramEncoder);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 645 */         paramEncoder.getExceptionListener().exceptionThrown(localException);
/*     */       }
/*     */     }
/* 648 */     for (k = j; k < i; k++)
/* 649 */       invokeStatement(paramObject1, "add", new Object[] { localList1.get(k) }, paramEncoder);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_util_List_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */