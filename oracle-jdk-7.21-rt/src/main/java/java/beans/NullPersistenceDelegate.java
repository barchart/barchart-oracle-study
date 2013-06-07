/*    */ package java.beans;
/*    */ 
/*    */ class NullPersistenceDelegate extends PersistenceDelegate
/*    */ {
/*    */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*    */   {
/*    */   }
/*    */ 
/*    */   protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*    */   {
/* 78 */     return null;
/*    */   }
/*    */ 
/*    */   public void writeObject(Object paramObject, Encoder paramEncoder)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.NullPersistenceDelegate
 * JD-Core Version:    0.6.2
 */