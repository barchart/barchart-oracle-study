/*    */ package java.beans;
/*    */ 
/*    */ class EnumPersistenceDelegate extends PersistenceDelegate
/*    */ {
/*    */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*    */   {
/* 92 */     return paramObject1 == paramObject2;
/*    */   }
/*    */ 
/*    */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 96 */     Enum localEnum = (Enum)paramObject;
/* 97 */     return new Expression(localEnum, Enum.class, "valueOf", new Object[] { localEnum.getDeclaringClass(), localEnum.name() });
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.EnumPersistenceDelegate
 * JD-Core Version:    0.6.2
 */