/*     */ package java.beans;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ class java_util_Collection_PersistenceDelegate extends DefaultPersistenceDelegate
/*     */ {
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 608 */     Collection localCollection1 = (Collection)paramObject1;
/* 609 */     Collection localCollection2 = (Collection)paramObject2;
/*     */ 
/* 611 */     if (localCollection2.size() != 0) {
/* 612 */       invokeStatement(paramObject1, "clear", new Object[0], paramEncoder);
/*     */     }
/* 614 */     for (Iterator localIterator = localCollection1.iterator(); localIterator.hasNext(); )
/* 615 */       invokeStatement(paramObject1, "add", new Object[] { localIterator.next() }, paramEncoder);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_util_Collection_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */