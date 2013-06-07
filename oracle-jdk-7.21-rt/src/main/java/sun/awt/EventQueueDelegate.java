/*    */ package sun.awt;
/*    */ 
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.EventQueue;
/*    */ 
/*    */ public class EventQueueDelegate
/*    */ {
/* 33 */   private static final Object EVENT_QUEUE_DELEGATE_KEY = new StringBuilder("EventQueueDelegate.Delegate");
/*    */ 
/*    */   public static void setDelegate(Delegate paramDelegate)
/*    */   {
/* 37 */     AppContext.getAppContext().put(EVENT_QUEUE_DELEGATE_KEY, paramDelegate);
/*    */   }
/*    */   public static Delegate getDelegate() {
/* 40 */     return (Delegate)AppContext.getAppContext().get(EVENT_QUEUE_DELEGATE_KEY);
/*    */   }
/*    */ 
/*    */   public static abstract interface Delegate
/*    */   {
/*    */     public abstract AWTEvent getNextEvent(EventQueue paramEventQueue)
/*    */       throws InterruptedException;
/*    */ 
/*    */     public abstract Object beforeDispatch(AWTEvent paramAWTEvent)
/*    */       throws InterruptedException;
/*    */ 
/*    */     public abstract void afterDispatch(AWTEvent paramAWTEvent, Object paramObject)
/*    */       throws InterruptedException;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.EventQueueDelegate
 * JD-Core Version:    0.6.2
 */