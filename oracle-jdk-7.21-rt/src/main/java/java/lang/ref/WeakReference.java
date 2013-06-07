/*    */ package java.lang.ref;
/*    */ 
/*    */ public class WeakReference<T> extends Reference<T>
/*    */ {
/*    */   public WeakReference(T paramT)
/*    */   {
/* 57 */     super(paramT);
/*    */   }
/*    */ 
/*    */   public WeakReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue)
/*    */   {
/* 69 */     super(paramT, paramReferenceQueue);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ref.WeakReference
 * JD-Core Version:    0.6.2
 */