/*    */ package sun.tracing;
/*    */ 
/*    */ class NullProbe extends ProbeSkeleton
/*    */ {
/*    */   public NullProbe(Class<?>[] paramArrayOfClass)
/*    */   {
/* 74 */     super(paramArrayOfClass);
/*    */   }
/*    */ 
/*    */   public boolean isEnabled() {
/* 78 */     return false;
/*    */   }
/*    */ 
/*    */   public void uncheckedTrigger(Object[] paramArrayOfObject)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.tracing.NullProbe
 * JD-Core Version:    0.6.2
 */