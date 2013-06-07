/*     */ package java.beans;
/*     */ 
/*     */ import java.util.Date;
/*     */ 
/*     */ class java_util_Date_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 272 */     if (!super.mutatesTo(paramObject1, paramObject2)) {
/* 273 */       return false;
/*     */     }
/* 275 */     Date localDate1 = (Date)paramObject1;
/* 276 */     Date localDate2 = (Date)paramObject2;
/*     */ 
/* 278 */     return localDate1.getTime() == localDate2.getTime();
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 282 */     Date localDate = (Date)paramObject;
/* 283 */     return new Expression(localDate, localDate.getClass(), "new", new Object[] { Long.valueOf(localDate.getTime()) });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_util_Date_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */