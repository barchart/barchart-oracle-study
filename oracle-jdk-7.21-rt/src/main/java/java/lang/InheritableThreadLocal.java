/*    */ package java.lang;
/*    */ 
/*    */ public class InheritableThreadLocal<T> extends ThreadLocal<T>
/*    */ {
/*    */   protected T childValue(T paramT)
/*    */   {
/* 62 */     return paramT;
/*    */   }
/*    */ 
/*    */   ThreadLocal.ThreadLocalMap getMap(Thread paramThread)
/*    */   {
/* 71 */     return paramThread.inheritableThreadLocals;
/*    */   }
/*    */ 
/*    */   void createMap(Thread paramThread, T paramT)
/*    */   {
/* 82 */     paramThread.inheritableThreadLocals = new ThreadLocal.ThreadLocalMap(this, paramT);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.InheritableThreadLocal
 * JD-Core Version:    0.6.2
 */