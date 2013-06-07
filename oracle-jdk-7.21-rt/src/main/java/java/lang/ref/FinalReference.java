/*    */ package java.lang.ref;
/*    */ 
/*    */ class FinalReference<T> extends Reference<T>
/*    */ {
/*    */   public FinalReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue)
/*    */   {
/* 34 */     super(paramT, paramReferenceQueue);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ref.FinalReference
 * JD-Core Version:    0.6.2
 */