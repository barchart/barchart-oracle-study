/*    */ package java.util;
/*    */ 
/*    */ public abstract class EventListenerProxy<T extends EventListener>
/*    */   implements EventListener
/*    */ {
/*    */   private final T listener;
/*    */ 
/*    */   public EventListenerProxy(T paramT)
/*    */   {
/* 64 */     this.listener = paramT;
/*    */   }
/*    */ 
/*    */   public T getListener()
/*    */   {
/* 73 */     return this.listener;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.EventListenerProxy
 * JD-Core Version:    0.6.2
 */